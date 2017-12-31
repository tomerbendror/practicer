package com.practice.dto.group;

import com.practice.model.*;

import java.util.*;

/**
 * User: tomer
 */
public class ParentsGroupDto {
    private Long id;
    private String name;
    private String description;
    private boolean imManager;
    private List<ParentInGroupDto> members;
    private List<ChildInGroupDto> myChilds;

    public ParentsGroupDto(ParentsGroup group, ParentUser parent, Map<String, ParentInGroupDto> emailToFullName) {
        id = group.getId();
        name = group.getName();
        description = group.getDescription();

        Set<Long> groupChildsIds = new HashSet<>();
        for (ChildToGroup childToGroup : group.getChildsToGroup()) {
            groupChildsIds.add(childToGroup.getChildUser().getId());
        }
        List<ChildUser> childs = parent.getChilds();
        myChilds = new ArrayList<>(childs.size());
        for (ChildUser child : childs) {
            myChilds.add(new ChildInGroupDto(child, groupChildsIds.contains(child.getId())));
        }

        members = new ArrayList<>(group.getGroupInvitations().size() + group.getGroupsToUsers().size());
        Set<String> membersEmails = new HashSet<>(group.getGroupsToUsers().size());
        for (ParentToGroup parentToGroup : group.getGroupsToUsers()) {
            members.add(new ParentInGroupDto(parentToGroup));
            membersEmails.add(parentToGroup.getParentUser().getEmail());
            if (parentToGroup.getUserInGroupRole().isManager() && parentToGroup.getParentUser().getId().equals(parent.getId())) {
                imManager = true;
            }
        }

        for (GroupInvitation groupInvitation : group.getGroupInvitations()) {
            // check the isWasUsed since it's possible that we send invitation for a mail address but the user register with other mail address
            if (!groupInvitation.isWasUsed() && !membersEmails.contains(groupInvitation.getEmail())) { // want to add the invites only in case the user hasn't registered
                ParentInGroupDto parentInGroupDto = new ParentInGroupDto(groupInvitation);
                ParentInGroupDto existParent = emailToFullName.get(parentInGroupDto.getEmail());
                if (existParent != null) {
                    parentInGroupDto.setUserId(existParent.getUserId());
                    parentInGroupDto.setFullName(existParent.getFullName());
                    parentInGroupDto.setProfileImageUrl(existParent.getProfileImageUrl());
                }
                members.add(parentInGroupDto);
            }
        }

        // alphabet
        Collections.sort(members, new Comparator<ParentInGroupDto>() {
            @Override
            public int compare(ParentInGroupDto parent1, ParentInGroupDto parent2) {
                return parent1.getEmail().compareTo(parent2.getEmail());
            }
        });

        // manager, member, invitee
        Collections.sort(members, new Comparator<ParentInGroupDto>() {
            @Override
            public int compare(ParentInGroupDto parent1, ParentInGroupDto parent2) {
                if ((parent1.getManager() && !parent2.getManager()) || (parent1.getMember() && !parent2.getMember()) || (parent1.getFullName() != null  && parent2.getFullName() == null)) {
                    return -1;
                } else if ((parent2.getManager() && !parent1.getManager()) || (parent2.getMember() && !parent1.getMember()) || (parent2.getFullName() != null  && parent1.getFullName() == null)) {
                    return 1;
                }
                return 0;
            }
        });
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ParentInGroupDto> getMembers() {
        return members;
    }

    public void setMembers(List<ParentInGroupDto> members) {
        this.members = members;
    }

    public boolean isImManager() {
        return imManager;
    }

    public void setImManager(boolean imManager) {
        this.imManager = imManager;
    }

    public List<ChildInGroupDto> getMyChilds() {
        return myChilds;
    }

    public void setMyChilds(List<ChildInGroupDto> myChilds) {
        this.myChilds = myChilds;
    }
}
