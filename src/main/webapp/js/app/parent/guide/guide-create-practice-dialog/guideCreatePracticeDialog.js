angular.module('parentApp').controller('guideCreatePracticeDialogController',
    function modalController ($scope, $modalInstance, $location) {
        $scope.done = function () {
            $modalInstance.close();
        };

        $scope.openCreatePracticeHelp = function () {
            $modalInstance.close();
            if (!isMobileOrTablet()) {
                inline_manual_player.activateTopic(11121);
            }
        };
    });