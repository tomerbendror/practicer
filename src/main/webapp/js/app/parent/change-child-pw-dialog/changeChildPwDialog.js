angular.module('parentApp').controller('changeChildPwDialogController',
    function modalController ($scope, $http, $modalInstance, $timeout, parentDataService, childId) {
        $scope.childId = childId;
        $scope.showBusy = false;

        $scope.done = function () {
            if ($scope.validate()) {
                $scope.showBusy = true;
                parentDataService.changeChildPassword({id: $scope.childId, password: $scope.password}).then(function () {
                    toastr.success('הסיסמה עודכנה בהצלחה');
                    $modalInstance.close();
                }, function() {
                    $scope.showBusy = false;
                });
            }
        };

        $scope.validate = function () {
            if (!$scope.password || $scope.password.length < 6) {
                return $scope.error('יש להזין סיסמה בת 6 תווים לפחות');
            }

            if ($scope.password !== $scope.rePassword) {
                return $scope.error('הסיסמה בשדה אימות סיסמה אינה תואמת את הסיסמה');
            }

            $('#edit-question-error-panel').slideUp();
            $scope.errorMsg = '';
            return true;
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.error = function (msg) {
            $scope.errorMsg = msg;
            $('#edit-question-error-panel').slideDown();
            return false;
        };
    });