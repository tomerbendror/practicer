function childInfoPanel($timeout) {
    return {
        scope: {
            child: "="
        },  // use a new isolated scope
        restrict: 'AE',
        replace: 'false',
        templateUrl: '/html/app/directives/childInfoPanel.html',
        controller: childInfoPanelCtrl,
        link: function ($scope, element) {
            $timeout(function(){    // kind of invoke later
                onChildInfoLinked($scope, $(element));

                $scope.toggleEdit = function() {
                    $(element).find('#user .editable').editable('toggleDisabled');
                };
            }, 0);
        }
    };
}

function childInfoPanelCtrl($scope, $timeout, $http, $modal, parentDataService, contextService) {
    $scope.savingChild = false;
    $scope.enterAsChildStr = $scope.child.gender == 'FEMALE' ? 'כניסה לאפליקציה בתור הילדה' : 'כניסה לאפליקציה בתור הילד';

    $scope.editBtn = function() {
        $scope.dataBeforeEdit = JSON.parse(angular.toJson($scope.child));
        privateToggleEdit();
    };

    $scope.validateCreate = function() {
        var child = $scope.child;
        if ($scope.validateUpdate()) {
            if (!child.password || child.password.length < 6) {
                return error('יש להזין סיסמה בת 6 תווים לפחות')
            }
            if (child.password !== child.rePassword) {
                return error('הסיסמה בשדה אימות סיסמה אינה תואמת את הסיסמה');
            }
            $scope.child.errorMsg = '';
            return true;
        }
        return false;
    };

    $scope.validateUpdate = function() {
        var child = $scope.child;
        if (!child.userName || child.userName.length < 6) {
            return error('יש להזין שם משתמש בן 6 תווים לפחות')
        }
        if (!child.firstName || child.firstName.length < 2) {
            return error('יש להזין שם פרטי')
        }
        if (!child.lastName || child.lastName.length < 2) {
            return error('יש להזין שם משפחה')
        }
        $scope.child.errorMsg = '';
        return true;
    };

    function error(msg) {
        $scope.child.errorMsg = msg;
        return false;
    }

    $scope.saveChanges = function() {
        var child = $scope.child;
        if (child.newUser) {
            if (!$scope.validateCreate()) {
                return;
            }

            $scope.savingChild = true;
            parentDataService.createChild(child)
                .then(function (createdChild) {
                    $scope.$parent.childs[0] = createdChild;
                    if (createdChild.gender == 'FEMALE') {
                        toastr.success('המשתמשת נוצרה בהצלחה');
                    } else {
                        toastr.success('המשתמש נוצר בהצלחה');
                    }
                    $scope.$parent.setEnableAddBtn(true);
                    $scope.savingChild = false;

                    parentDataService.getParentProfile(true).then(function(parentProfile) {
                        if (parentProfile.childCount == 1) {    // we assume this user have just create his first child
                            $modal.open({
                                templateUrl: '/js/app/parent/guide/guide-create-practice-dialog/guideCreatePracticeDialog.html',
                                controller: 'guideCreatePracticeDialogController',
                                keyboard: false,
                                backdrop: 'static'
                            });
                        }
                    });
                }, function() {
                    $scope.savingChild = false;
                });
        } else {
            if (!$scope.validateUpdate()) {
                return;
            }
            $scope.savingChild = true;
            parentDataService.updateChild(child)
                .then(function (updatedChild) {
                    var childsAry = $scope.$parent.childs;
                    for (var i = 0; i < childsAry.length; i++) {
                        var childElement = childsAry[i];
                        if (childElement.id == updatedChild.id) {
                            childsAry[i] = updatedChild;
                            break;
                        }
                    }
                    if (updatedChild.gender == 'FEMALE') {
                        toastr.success('המשתמשת עודכנה בהצלחה');
                    } else {
                        toastr.success('המשתמש עודכן בהצלחה');
                    }
                    $scope.$parent.setEnableAddBtn(true);
                    $scope.savingChild = false;
                }, function() {
                    $scope.savingChild = false;
                });
        }
    };

    $scope.changePassword = function() {
        $modal.open({
            templateUrl: '/js/app/parent/change-child-pw-dialog/changeChildPwDialog.html',
            controller: 'changeChildPwDialogController',
            keyboard: false,
            backdrop: 'static',
            //size: 'lg',
            resolve: {
                'childId': function() { return $scope.child.id; }
            }
        });
    };

    $scope.cancel = function() {
        var beforeEdit = $scope.dataBeforeEdit;
        var childsAry = $scope.$parent.childs;

        for (var i = 0; i < childsAry.length; i++) {
            var childElement = childsAry[i];
            if (childElement.id == beforeEdit.id) {
                childsAry[i] = beforeEdit;
                return;
            }
        }
    };

    $scope.viewChildApp = function() {
        $http.get('/app/parent/' + contextService.getUserId() + '/viewAsChild?childId=' + $scope.child.id).success(function (response) {
            $timeout(function() {window.location.replace(response.responseObject);}, 10);
        });
    };

    $scope.deleteChild = function() {
        var childToRemove = $scope.child;
        if (childToRemove.newUser) {    // added but not saved yet
            bootbox.confirm("האם לבטל את הוספת הילד/ה?", function(userAnswer) {
                if (userAnswer) {
                    $scope.$parent.childs.splice(0, 1);
                    $scope.$parent.setEnableAddBtn(true);
                    $scope.$apply();
                }
            });
        } else {
            bootbox.confirm("האם להסיר את המשתמש '"+ childToRemove.firstName + "' לצמיתות?", function(userAnswer) {
                if (userAnswer) {
                    parentDataService.deleteChild(childToRemove)
                        .then(function (removedChild) {
                            removeFromChildsAry(removedChild.id);
                            if (removedChild.gender == 'FEMALE') {
                                toastr.success('הילדה הוסרה בהצלחה' );
                            } else {
                                toastr.success('הילד הוסר בהצלחה' );
                            }
                        });
                }
            });
        }
    };

    // remove the element from UI after the response from the server
    function removeFromChildsAry(childId) {
        var childsAry = $scope.$parent.childs;
        for (var i = 0; i < childsAry.length; i++) {
            var childElement = childsAry[i];
            if (childElement.id == childId) {
                childsAry.splice(i, 1);
                $scope.$parent.setEnableAddBtn(true);
                return;
            }
        }
    }

    function privateToggleEdit() {
        $scope.editMode = !$scope.editMode;
        $scope.toggleEdit();
    }
}

function onChildInfoLinked(childScope, element) {
    var $panel = element.find(".panel"),
        data = $panel.data(),
        options = {};

    options.collapse = ( data.collapse ) ? true : false;
    options.expand = ( data.expand ) ? true : false;
    options.color = ( data.color ) ? data.color : 'none';
    // init
    $panel.wrapkitPanel(options);

    editable(childScope, element);
}

function editable(childScope, element){
    'use strict';

    var userName = element.find('#username');
    userName.editable({
        validate: function(value) {
            return validateChildUserName(value);
        }
    });
    userName.bind('contentChanged', function(event, newValue) {
        childScope.child.userName = newValue;
    });

    var password = element.find('#password');
    password.editable({
        validate: function(value) {
            return validateNotEmpty(value);
        }
    });
    password.bind('contentChanged', function(event, newValue) {
        childScope.child.password = newValue;
    });

    var rePassword = element.find('#re-password');
    rePassword.editable({
        validate: function(value) {
            return validateNotEmpty(value);
        }
    });
    rePassword.bind('contentChanged', function(event, newValue) {
        childScope.child.rePassword = newValue;
    });

    var firstName = element.find('#firstName');
    firstName.editable({
        validate: function(value) {
            return validateNotEmpty(value);
        }
    });
    firstName.bind('contentChanged', function(event, newValue) {
        childScope.child.firstName = newValue;
    });

    var lastName = element.find('#lastName');
    lastName.editable({
        validate: function(value) {
            return validateNotEmpty(value);
        }
    });
    lastName.bind('contentChanged', function(event, newValue) {
        childScope.child.lastName = newValue;
    });

    var gender = element.find('#sex');
    gender.editable({
        inputclass: 'select',
        source: [
            {value: 'MALE', text: 'זכר'},
            {value: 'FEMALE', text: 'נקבה'}
        ]
    });
    gender.bind('contentChanged', function(event, newValue) {
        childScope.child.gender = newValue;
    });

    var birthDate = element.find('#dob');
    birthDate.editable();
    birthDate.bind('contentChanged', function(event, newValue) {
        childScope.child.birthDate = newValue.format("MM/DD/YYYY HH:mm");
    });

    if (childScope.child.newUser) {
        element.find('#user .editable').editable('enable');
    } else {
        element.find('#user .editable').editable('disable');
    }
}

function validateNotEmpty(value) {
    if($.trim(value) === '') {
        return 'שדה זה הינו חובה';
    }
}

function validateChildUserName(value) {
    if($.trim(value) === '') {
        return 'שדה זה הינו חובה';
    }

    if (value.indexOf("@") > -1) {
        return "אין להשתמש בתו '@'";
    }
}