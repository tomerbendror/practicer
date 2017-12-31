package com.practice.repository;

import com.google.common.collect.Sets;
import com.practice.controller.EmailController;
import com.practice.dto.group.ChildInGroupDto;
import com.practice.dto.group.GroupInvitesDto;
import com.practice.dto.group.ParentInGroupDto;
import com.practice.dto.group.ParentsGroupDto;
import com.practice.etc.error.LogicalException;
import com.practice.model.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

/**
 * User: tomer
 */
@Repository("groupRepository")
@Transactional
public class GroupRepository extends BaseRepository {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private EmailController emailController;

    public ParentsGroupDto createGroup(Long creatorId, ParentsGroup parentsGroup) {
        ParentUser creator = parentRepository.getParent(creatorId);
        ParentToGroup parentToGroup = new ParentToGroup(creator, parentsGroup, UserInGroupRole.MANAGER);

        GroupInvitation inviterGroupInvitation = null;
        Date inviteDate = new Date();
        Set<GroupInvitation> groupInvitations = parentsGroup.getGroupInvitations();
        for (GroupInvitation groupInvitation : groupInvitations) {
            String email = groupInvitation.getEmail();
            if (StringUtils.isNotBlank(email)) {
                if (email.trim().equalsIgnoreCase(creator.getEmail())) {
                    inviterGroupInvitation = groupInvitation;
                } else {
                    groupInvitation.setParentsGroup(parentsGroup);
                    groupInvitation.setInviteDate(inviteDate);
                    groupInvitation.setInviter(creator);
                }
            }
        }
        if (inviterGroupInvitation != null) {
            groupInvitations.remove(inviterGroupInvitation);    // if the creator email is part of the invitation, remove it
        }

        parentsGroup.setGroupsToUsers(Sets.newHashSet(parentToGroup));   // add the creator parent as member of the group
        creator.getParentToGroups().add(parentToGroup);

        List<ChildUser> childs = creator.getChilds();
        if (childs.size() == 1) {   // currently for most of the case parent will have only single child, so we help him
            parentsGroup.getChildsToGroup().add(new ChildToGroup(childs.get(0), parentsGroup));
        }

        genericDao.persist(parentsGroup);
        genericDao.flush();

        inviteToGroup(creatorId, parentsGroup.getId(), parentsGroup.getGroupInvitations());

        return getGroupDto(parentsGroup, creatorId);
    }

    public void acceptInvitations(Long parentId, List<Long> invitationsIds) {
        List<GroupInvitation> unusedInvitations = getUnusedInvitations(parentId);

        for (Long invitationsId : invitationsIds) {
            for (GroupInvitation groupInvitation : unusedInvitations) {
                if (groupInvitation.getId().equals(invitationsId)) {
                    addParentToGroup(parentId, groupInvitation.getParentsGroup().getId(), groupInvitation.getInvitationKey());
                }
            }
        }
    }

    public boolean addParentToGroup(Long parentId, Long groupId, String invitationKey) {
        ParentUser parentUser = getEm().find(ParentUser.class, parentId);
        String sql = "from GroupInvitation gi where gi.parentsGroup.id=:groupId and gi.invitationKey=:invitationKey";
        TypedQuery<GroupInvitation> query = getEm().createQuery(sql, GroupInvitation.class);
        query.setParameter("invitationKey", invitationKey);
        query.setParameter("groupId", groupId);
        GroupInvitation existGroupInvitation = genericDao.getSingleResult(query);
        if(existGroupInvitation == null){
            throw new LogicalException(String.format("The group for invitation key %s doesn't exist", invitationKey));
        } else {
            ParentsGroup group = existGroupInvitation.getParentsGroup();
            existGroupInvitation.setWasUsed(true);
            ParentToGroup parentToGroup = new ParentToGroup(parentUser, group, UserInGroupRole.MEMBER);
            Set<ParentToGroup> groupsToUsers = group.getGroupsToUsers();
            Hibernate.initialize(groupsToUsers);
            for (ParentToGroup existParentToGroup : groupsToUsers) {  // groupsToUsers is not a regular set but Hibernate one, need to verify the contain manually
                if (existParentToGroup.equals(parentToGroup)) {
                    return false;   // parent already a member in this group
                }
            }
            groupsToUsers.add(parentToGroup);

            List<ChildUser> childs = parentUser.getChilds();
            if (childs.size() == 1) {   // currently for most of the case parent will have only single child, so we help him
                group.getChildsToGroup().add(new ChildToGroup(childs.get(0), group));
            }

            emailController.sendParentJoinToGroupMail(parentUser, group);
            return true;
        }
    }

    public GroupInvitesDto reInviteToGroup(Long parentId, Long groupId, List<User> users) {
        Set<GroupInvitation> invitations = new HashSet<>(users.size());
        for (User user : users) {
            GroupInvitation groupInvitation = new GroupInvitation();
            groupInvitation.setEmail(user.getEmail());
            invitations.add(groupInvitation);
        }
        return inviteToGroup(parentId, groupId, invitations);
    }

    public ParentsGroupDto setParentInGroupRole(Long groupId, Long parentId, UserInGroupRole newRole) {
        ParentsGroup group = getGroup(groupId);
        for (ParentToGroup parentToGroup : group.getGroupsToUsers()) {
            if (parentToGroup.getParentUser().getId().equals(parentId)) {
                parentToGroup.setUserInGroupRole(newRole);
                break;
            }
        }
        return getGroupDto(group, getUserDetails().getId());
    }

    public GroupInvitesDto inviteToGroup(Long parentId, Long groupId, Set<GroupInvitation> invitations) {
        int newInvites = 0;
        int resendInvites = 0;

        ParentsGroup existGroup = genericDao.find(ParentsGroup.class, groupId);

        // fill the current group member emails
        Set<String> existUsersMails = new HashSet<>(existGroup.getGroupsToUsers().size());
        for (ParentToGroup parentToGroup : existGroup.getGroupsToUsers()) {
            existUsersMails.add(parentToGroup.getParentUser().getEmail());
        }
        Date inviteDate = new Date();

        // fill to the new invitation the parent and group
        ParentUser parent = parentRepository.getParent(parentId);
        for (GroupInvitation newInvitation : invitations) {
            String key = new BigInteger(130, new SecureRandom()).toString(32);

            newInvitation.setInviter(parent);
            newInvitation.setParentsGroup(existGroup);
            newInvitation.setInvitationKey(key);
            newInvitation.setInviteDate(inviteDate);
        }

        Set<GroupInvitation> invitationToSend = new HashSet<>();
        for (GroupInvitation newInvitation : invitations) {
            GroupInvitation resendInvitation = null;
            for (GroupInvitation existInvitation : existGroup.getGroupInvitations()) {
                // we found an invitation we already have, and the invitee was not join yet to this group
                if (existInvitation.equals(newInvitation) && !existUsersMails.contains(newInvitation.getEmail())) {
                    resendInvitation = existInvitation;
                    resendInvitation.setInvitationKey(newInvitation.getInvitationKey());
                    resendInvitation.setWasUsed(false);
                    break;
                }
            }

            if (resendInvitation != null) {
                resendInvitation.setInviteDate(inviteDate);
                invitationToSend.add(resendInvitation);
                resendInvites++;
            } else if (!existUsersMails.contains(newInvitation.getEmail())) {
                invitationToSend.add(newInvitation);
                existGroup.getGroupInvitations().add(newInvitation);
                newInvites++;
            }
        }

        emailController.sendGroupInviteEmail(parent, invitationToSend, existGroup);
        return new GroupInvitesDto(getGroupDto(existGroup, parentId), newInvites, resendInvites);
    }

    public ParentsGroupDto updateGroup(Long userId, ParentsGroup parentsGroup) {
        ParentsGroup existGroup = genericDao.find(ParentsGroup.class, parentsGroup.getId());
        existGroup.setName(parentsGroup.getName());
        existGroup.setDescription(parentsGroup.getDescription());
        return getGroupDto(existGroup, userId);
    }

    public ParentsGroup removeGroup(Long groupId) {
        ParentsGroup parentsGroup = genericDao.find(ParentsGroup.class, groupId);
        genericDao.remove(ParentsGroup.class, groupId);
        return parentsGroup;
    }


    public ParentsGroupDto removeParents(Long groupId, ParentInGroupDto parentInGroupDto) {
        ParentsGroup parentsGroup = genericDao.find(ParentsGroup.class, groupId);

        // for the general case there are two option, the user is member and there is no invitation, the user is not member and we have invitation
        // here we check both options, just in case
        for (GroupInvitation invitation : parentsGroup.getGroupInvitations()) {
            if (invitation.getEmail().equalsIgnoreCase(parentInGroupDto.getEmail())) {
                parentsGroup.getGroupInvitations().remove(invitation);
                break;
            }
        }

        if (parentInGroupDto.getUserId() != null) {
            for (ParentToGroup parentToGroup : parentsGroup.getGroupsToUsers()) {
                ParentUser parentUser = parentToGroup.getParentUser();
                if (parentUser.getId().equals(parentInGroupDto.getUserId())) {
                    for (ChildUser child : parentUser.getChilds()) {
                        Set<ChildToGroup> childToGroups = child.getChildToGroups();
                        for (ChildToGroup childToGroup : childToGroups) {
                            if (childToGroup.getParentsGroup().getId().equals(groupId)) {
                                childToGroups.remove(childToGroup);
                                break;
                            }
                        }
                    }
                    parentsGroup.getGroupsToUsers().remove(parentToGroup);
                    parentUser.getParentToGroups().remove(parentToGroup);    // the userToGroup should be remove as it's now orphan from the user side
                    break;
                }
            }
        }
        return getGroupDto(parentsGroup, getUserDetails().getId());
    }

    private Map<String, ParentInGroupDto> getExistUsersMap(ParentsGroup... parentsGroups) {
        // get the details of users that are registered to the application and were invited but not join yet
        Set<String> inviteeEmails = new HashSet<>();
        for (ParentsGroup group : parentsGroups) {
            for (GroupInvitation invitation : group.getGroupInvitations()) {
                inviteeEmails.add(invitation.getEmail());
            }
        }

        Map<String, ParentInGroupDto> existUsersMap = new HashMap<>();
        if (!inviteeEmails.isEmpty()) {
            TypedQuery<ParentUser> query = getEm().createQuery("from ParentUser where email in :inviteeEmails", ParentUser.class);
            query.setParameter("inviteeEmails", inviteeEmails);
            for (ParentUser parentUser : query.getResultList()) {
                ParentInGroupDto parentInGroupDto = new ParentInGroupDto();
                parentInGroupDto.setUserId(parentUser.getId());
                parentInGroupDto.setFullName(parentUser.getFullName());
                parentInGroupDto.setProfileImageUrl(parentUser.getProfileImageUrlOrDefault());
                parentInGroupDto.setGender(parentUser.getGender());
                existUsersMap.put(parentUser.getEmail(), parentInGroupDto);
            }
        }
        return existUsersMap;
    }

    public ParentsGroupDto getGroupDto(ParentsGroup parentsGroup, Long parentId) {
        Map<String, ParentInGroupDto> emailToFullName = getExistUsersMap(parentsGroup);
        ParentUser parent = parentRepository.getParent(parentId);
        return new ParentsGroupDto(parentsGroup, parent, emailToFullName);
    }

    public List<ParentsGroupDto> getGroupDtos(Long parentId) {
        List<ParentsGroup> groups = getGroups(parentId);
        Map<String, ParentInGroupDto> emailToParentInGroupDtos = getExistUsersMap(groups.toArray(new ParentsGroup[groups.size()]));

        List<ParentsGroupDto> groupDtos = new ArrayList<>(groups.size());
        ParentUser parent = parentRepository.getParent(parentId);
        for (ParentsGroup group : groups) {
            groupDtos.add(new ParentsGroupDto(group, parent, emailToParentInGroupDtos));
        }

        Collections.sort(groupDtos, new Comparator<ParentsGroupDto>() {
            @Override
            public int compare(ParentsGroupDto groupDto1, ParentsGroupDto groupDto2) {
                return groupDto1.getName().compareTo(groupDto2.getName());
            }
        });
        return groupDtos;
    }

    public List<ParentsGroup> getGroups(Long parentId) {
        ParentUser parent = genericDao.find(ParentUser.class, parentId);
        Set<ParentToGroup> usersToGroups = parent.getParentToGroups();
        List<ParentsGroup> groups = new ArrayList<>(usersToGroups.size());
        for (ParentToGroup usersToGroup : usersToGroups) {
            groups.add(usersToGroup.getParentsGroup());
        }
        return groups;
    }

    public ParentsGroup getGroup(Long groupId) {
        return genericDao.find(ParentsGroup.class, groupId);
    }

    @SuppressWarnings("unused")
    public boolean isGroupManager(Long groupId) {
        ParentsGroup parentsGroup = genericDao.find(ParentsGroup.class, groupId);
        if (parentsGroup != null) {
            Long parentId = getUserDetails().getId();
            for (ParentToGroup parentToGroup : parentsGroup.getGroupsToUsers()) {
                if (parentToGroup.getParentUser().getId().equals(parentId) && parentToGroup.getUserInGroupRole().isManager()) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public boolean isGroupMember(Long groupId) {
        ParentsGroup parentsGroup = genericDao.find(ParentsGroup.class, groupId);
        if (parentsGroup != null) {
            Long parentId = getUserDetails().getId();
            for (ParentToGroup parentToGroup : parentsGroup.getGroupsToUsers()) {
                if (parentToGroup.getParentUser().getId().equals(parentId) && parentToGroup.getUserInGroupRole() != UserInGroupRole.DELETED) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<GroupInvitation> getUnusedInvitations(Long parentId) {
        ParentUser parent = parentRepository.getParent(parentId);
        TypedQuery<GroupInvitation> query = getEm().createQuery("from GroupInvitation where email=:parentMail and wasUsed=false ", GroupInvitation.class);
        query.setParameter("parentMail", parent.getEmail());
        return query.getResultList();
    }

    public List<ChildInGroupDto> updateChildInGroup(Long groupId, Long parentId, List<ChildInGroupDto> myChilds) {
        ParentsGroup group = getGroup(groupId);
        for (ChildInGroupDto myChild : myChilds) {
            Long childId = myChild.getChildId();
            ChildUser child = childRepository.getChild(childId);
            if (child.isChildOf(parentId)) {
                if (myChild.getInGroup()) {     // exist or need to add
                    if (!childExistInGroup(group, childId)) {
                        ChildToGroup childToGroup = new ChildToGroup(child, group);
                        group.getChildsToGroup().add(childToGroup);
                        child.getChildToGroups().add(childToGroup);
                    }
                } else {    // not in group - if exist need to remove it else do nothing
                    if (childExistInGroup(group, childId)) {
                        for (ChildToGroup childToGroup : group.getChildsToGroup()) {
                            if (childToGroup.getChildUser().getId().equals(childId)) {
                                group.getChildsToGroup().remove(childToGroup);
                                child.getChildToGroups().remove(childToGroup);
                                break;
                            }
                        }
                    }
                }
            }
        }

        Set<Long> groupChildsIds = new HashSet<>();
        for (ChildToGroup childToGroup : group.getChildsToGroup()) {
            groupChildsIds.add(childToGroup.getChildUser().getId());
        }
        ParentUser parent = parentRepository.getParent(parentId);
        List<ChildUser> childs = parent.getChilds();
        myChilds = new ArrayList<>(childs.size());
        for (ChildUser child : childs) {
            myChilds.add(new ChildInGroupDto(child, groupChildsIds.contains(child.getId())));
        }
        return myChilds;
    }

    private boolean childExistInGroup(ParentsGroup group, Long childId) {
        for (ChildToGroup childToGroup : group.getChildsToGroup()) {
            if (childToGroup.getChildUser().getId().equals(childId)) {
                return true;
            }
        }
        return false;
    }
}
