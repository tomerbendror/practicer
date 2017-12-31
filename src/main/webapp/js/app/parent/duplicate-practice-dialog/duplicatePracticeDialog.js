angular.module('parentApp').controller('duplicatePracticeDialogController',
    function modalController ($scope, $modalInstance, parentDataService, srcPractice) {
        $scope.title = 'שכפול תרגיל';
        $scope.errorMsg = '';
        $scope.inProcess = false;
        $scope.newPracticeName = srcPractice.name + ' - העתק';

        $scope.done = function () {
            if ($scope.validate()) {
                $scope.inProcess = true;
                parentDataService.duplicatePractice(srcPractice.id, $scope.newPracticeName)
                    .then(function (createPractice) {
                        $scope.inProcess = false;
                        $modalInstance.close(createPractice);
                    });
            }
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.validate = function () {
            if (isBlank($scope.newPracticeName)) {
                $scope.errorMsg = 'יש להזין שם לתרגיל';
                return false;
            }
            return true;
        };
    });