function groupInfoPanel($timeout) {
    return {
        scope: {
            group: "="
        },  // use a new isolated scope
        restrict: 'AE',
        replace: 'false',
        templateUrl: '/html/app/directives/groupInfoPanel.html',
        controller: groupInfoPanelCtrl,
        link: function ($scope, element) {
            $scope.element = $(element);
            $timeout(function(){onGroupInfoLinked($scope.element);}, 0);
        }
    };
}

function groupInfoPanelCtrl($scope, $timeout, parentDataService, contextService) {
    $scope.addMembersState = false;
    $scope.editGroupState = false;
    $scope.sendInviteEnable = true;
    $scope.updateGroupBtnEnable = true;
    $scope.childsInGroupChanged = false;
    $scope.enableSaveChildInGroupBtn = true;

    $scope.inviteEmails = [];
    $scope.updatedGroup = JSON.parse(angular.toJson($scope.group));
    $scope.updatedGroup.members = [];  // just in case

    $scope.originalMyChilds = JSON.parse(angular.toJson($scope.group.myChilds));

    $scope.message = ['MALE', 'FEMALE'];
    $scope.message['FEMALE'] = {
        managerRole: 'מנהלת קבוצה',
        memberRole: 'חברה בקבוצה',
        registerUser: 'משתמשת רשומה',
        notRegister: 'לא הצטרפה'
    };
    $scope.message['MALE'] = {
        managerRole: 'מנהל קבוצה',
        memberRole: 'חבר בקבוצה',
        registerUser: 'משתמש רשום',
        notRegister: 'לא הצטרף'
    };

    $scope.getText = function(gender, key) {
        if ("FEMALE" != gender) {
            gender = "MALE";
        }
        return $scope.message[gender][key];
    };

    $scope.childInGroupChanged = function() {
        var myChilds = $scope.group.myChilds;
        for (var i = 0; i < myChilds.length; i++) {
            var child = myChilds[i];
            for (var j = 0; j < $scope.originalMyChilds.length; j++) {
                var originalChild = $scope.originalMyChilds[j];
                if (originalChild.childId == child.childId && originalChild.inGroup != child.inGroup) {
                    $scope.childsInGroupChanged = true;
                    return;
                }
            }
        }
        $scope.childsInGroupChanged = false;
    };

    $scope.saveChildInGroupData = function() {
        $scope.enableSaveChildInGroupBtn = false;
        parentDataService.saveChildInGroupData($scope.group.id, $scope.group.myChilds)
            .then(function (updatedGroup) {
                var updatedChilds = updatedGroup.myChilds;
                toastr.success('ילדים בקבוצה עודכנו בהצלחה');

                var myChilds = $scope.group.myChilds;
                for (var i = 0; i < myChilds.length; i++) {
                    var child = myChilds[i];
                    for (var j = 0; j < updatedChilds.length; j++) {
                        var updated = updatedChilds[j];
                        if (updated.childId == child.childId) {
                            child.inGroup = updated.inGroup;
                        }
                    }
                }
                $scope.originalMyChilds = JSON.parse(angular.toJson($scope.group.myChilds));
                $scope.childsInGroupChanged = false;
                $scope.enableSaveChildInGroupBtn = true;
            }, function() {
                $scope.cancelChildInGroupEdit();
                $scope.enableSaveChildInGroupBtn = true;
            });
    };

    $scope.cancelChildInGroupEdit = function() {
        var myChilds = $scope.group.myChilds;
        for (var i = 0; i < myChilds.length; i++) {
            var child = myChilds[i];
            for (var j = 0; j < $scope.originalMyChilds.length; j++) {
                var originalChild = $scope.originalMyChilds[j];
                if (originalChild.childId == child.childId) {
                    child.inGroup = originalChild.inGroup;
                }
            }
        }
        $scope.childsInGroupChanged = false;
    };

    $scope.updateGroup = function() {
        if (!$scope.validateUpdateGroup()) {
            return;
        }
        $scope.updateGroupBtnEnable = false;
        $scope.group.groupInvitations = $scope.newGroupInvite;
        parentDataService.updateGroup($scope.updatedGroup)
            .then(function (updatedGroup) {
                $scope.updateGroupBtnEnable = true;
                $scope.group = updatedGroup;
                toastr.success('הקבוצה עודכנה בהצלחה');
                $scope.cancelEditGroup();
            }, function() {
                $scope.updateGroupBtnEnable = true;
            });
    };

    $scope.inviteAgain = function(member) {
        if (member.userId) {    // already join the group
            return;
        }
        parentDataService.inviteAgainToGroup($scope.group.id, member)
            .then(function (updatedGroup) {
                $scope.group = updatedGroup;
                toastr.success('ההזמנה נשלחה בהצלחה');
            });
    };

    $scope.setManager = function(member) {
        parentDataService.setParentToGroupManager($scope.group.id, member.userId)
            .then(function (updatedGroup) {
                $scope.group = updatedGroup;
            });
    };

    $scope.setTeacher = function(member) {
        parentDataService.setParentToGroupTeacher($scope.group.id, member.userId)
            .then(function (updatedGroup) {
                $scope.group = updatedGroup;
            });
    };

    $scope.sendInvite = function() {
        $timeout(function(){       // kind of invoke later that enable the email tag input to lose focus and validate the mail before calling the server
            if ($scope.inviteEmails.length == 0) {
                toastr.error('יש למלא כתובת מייל אחת לפחות');
                return;
            }

            $scope.sendInviteEnable = false;
            var updateGroupForInvite = JSON.parse(angular.toJson($scope.group));
            updateGroupForInvite.groupInvitations = $scope.inviteEmails;

            parentDataService.inviteToGroup(updateGroupForInvite)
                .then(function (responseObj) {
                    $scope.inviteEmails = [];
                    $scope.group = responseObj.updatedGroup;
                    var totalSend = (responseObj.newInvites + responseObj.resendInvites);
                    if (totalSend == 0) {
                        toastr.warning('לא נשלחו הזמנות, כל המוזמנים חברים בקבוצה');
                    } else if (totalSend == 1) {
                        toastr.success('הזמנה אחת נשלחה בהצלחה');
                    } else {
                        toastr.success(totalSend + ' הזמנות נשלחו בהצלחה' );
                    }
                    $scope.sendInviteEnable = true;
                    $scope.cancelAddMembers();
                }, function() {
                    $scope.sendInviteEnable = true;
                });
        }, 0);
    };

    $scope.isCurrentUser = function(member) {
        return contextService.getUserId() == member.userId;
    };

    $scope.validateUpdateGroup = function() {
        if ($.trim($scope.updatedGroup.name) === '') {
            return $scope.error('יש להזין שם לקבוצה')
        }
        return true;
    };

    $scope.getRole = function(member) {
        if (member.member) {
            if (member.manager) {
                return 'מנהל בקבוצה';
            } else {
                return 'חבר בקבוצה';
            }
        } else {
            return 'הוזמן';
        }
    };

    $scope.isRegisteredUser = function(member) {
        return $.trim(member.fullName) !== '';
    };

    $scope.editGroupBtn = function() {
        $scope.cancelAddMembers();

        $scope.editGroupState = true;
        $scope.element.find("#editGroupPanel").slideDown("slow", function() {
            $scope.element.find('#groupNameInput').focus();
        });
    };

    $scope.cancelEditGroup = function() {
        $scope.editGroupState = false;

        $scope.element.find("#editGroupPanel").slideUp("slow");
    };

    $scope.addMembersBtn = function() {
        $scope.cancelEditGroup();

        $scope.addMembersState = true;
        $scope.element.find("#addMembersPanel").slideDown("slow", function() {
            $scope.element.find('#inviteEmailsInput input').focus();
        });
    };

    $scope.cancelAddMembers = function() {
        $scope.addMembersState = false;
        $scope.element.find("#addMembersPanel").slideUp("slow", function() {
            $scope.inviteEmails = [];
        });
    };

    $scope.meGroupManager = function() {
        return $scope.group.imManager;
    };

    $scope.removeParentFromGroup = function(parent) {
        var message = parent.member ? "האם להסיר את המשתמש מהקבוצה?" : "האם לבטל את הזמנת המשתמש לקבוצה?" ;
        bootbox.confirm(message, function(userAnswer) {
            if (userAnswer) {
                parentDataService.removeParentFromGroup($scope.group.id, parent)
                    .then(function (updatedGroup) {
                        $scope.group = updatedGroup;
                        var message = parent.member ? 'המשתמש הוסר מהקבוצה' : 'הזמנת המשתמש לקבוצה בוטלה';
                        toastr.success(message);
                    });
            }
        });
    };

    $scope.deleteGroup = function() {
        bootbox.confirm("האם להסיר את הקבוצה '"+ $scope.group.name + "' לצמיתות?", function(userAnswer) {
            if (userAnswer) {
                parentDataService.deleteGroup($scope.group)
                    .then(function (removedGroup) {
                        removeFromGroupsAry(removedGroup.id);
                        toastr.success('הקבוצה הוסרה בהצלחה' );
                    });
            }
        });
    };

    // remove the element from UI after the response from the server
    function removeFromGroupsAry(groupId) {
        var groupsAry = $scope.$parent.groups;
        for (var i = 0; i < groupsAry.length; i++) {
            var groupElement = groupsAry[i];
            if (groupElement.id == groupId) {
                groupsAry.splice(i, 1);
                return;
            }
        }
    }

    $scope.error = function(msg) {
        $scope.errorMsg = msg;
        return false;
    };
}

function onGroupInfoLinked(element) {
    var $panel = element.find(".panel"),
        data = $panel.data(),
        options = {};

    options.collapse = ( data.collapse ) ? true : false;
    options.expand = ( data.expand ) ? true : false;
    options.color = ( data.color ) ? data.color : 'none';
    // init
    $panel.wrapkitPanel(options);
}