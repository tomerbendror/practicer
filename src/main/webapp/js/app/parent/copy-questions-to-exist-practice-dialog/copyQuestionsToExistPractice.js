angular.module('parentApp').controller('copyQuestionToExistPracticeDialogController',
    function modalController ($scope, $modalInstance, parentDataService, practice, possibleTargets) {
        $scope.title = 'העתקת שאלות לתרגיל קיים';
        $scope.possibleTargetPractices = possibleTargets;
        $scope.errorMsg = '';
        $scope.inProcess = false;

        $scope.done = function () {
            if ($scope.trgPractice) {
                $scope.inProcess = true;
                parentDataService.addQuestionsOfPractice($scope.trgPractice, practice)
                    .then(function (updatedPractice) {
                        $scope.inProcess = false;
                        $modalInstance.close(updatedPractice);
                    });
            } else {
                $scope.errorMsg = 'יש לבחור תרגיל תחילה';
            }
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    });