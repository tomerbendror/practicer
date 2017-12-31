function parentProfileCtrl($scope, $http, contextService, parentDataService) {
    $scope.saveBtnEnable = true;
    $scope.editMode = false;

    parentDataService.getParentProfile()
        .then(function (parentProfile) {
            $scope.parentProfile = parentProfile;
        });

    $scope.save = function() {
        if ($scope.validate()) {
            $scope.saveBtnEnable = false;
            parentDataService.updateParentProfile($scope.parentProfile)
                .then(function (parentProfile) {
                    $scope.parentProfile = parentProfile;
                    $scope.saveBtnEnable = true;
                    $scope.editMode = false;
                    toastr.success('ההגדרות האישיות נשמרו בהצלחה');
                }, function(errorMsg) {
                    $scope.showError(errorMsg);
                    $scope.saveBtnEnable = true;
                });
        }
    };

    $scope.cancel = function() {
        parentDataService.getParentProfile()    // rever the data
            .then(function (parentProfile) {
                $scope.parentProfile = parentProfile;
                $scope.editMode = false;
            });
    };

    $scope.edit = function() {
        $scope.editMode = true;
    };

    $scope.validate = function() {
        if ($scope.parentProfile.firstName.length > 15 || $scope.parentProfile.firstName.length < 2) {
            return $scope.showError("יש להזין שם פרטי בן 2-15 תווים");
        }

        if ($scope.parentProfile.lastName.length > 15 || $scope.parentProfile.lastName.length < 2) {
            return $scope.showError("יש להזין שם משפחה בן 2-15 תווים");
        }

        if (isBlank($scope.parentProfile.email)) {
            return $scope.showError("יש להזין ערך בשדה 'כתובת דוא''ל'");
        }

        if (!isValidEmailAddress($scope.parentProfile.email)) {
            return $scope.showError("יש להזין ערך חוקי בשדה 'כתובת דוא''ל'");
        }

        $scope.errorMsg = '';
        $('#parent-profile-error-panel').slideUp();
        return true;
    };

    $scope.showError = function(errorMsg) {
        $scope.errorMsg = errorMsg;
        $('#parent-profile-error-panel').slideDown();
        return false;
    };

    setActiveMenu('#profile-btn');
}