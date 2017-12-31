angular.module('parentApp').controller('yesNoModalController',
    function modalController ($scope, $modalInstance, title, message, yesText, noText) {
        $scope.title = title;
        $scope.message = message;
        $scope.yesText = yesText;
        $scope.noText = noText;

        $scope.done = function () {
            $modalInstance.close('yes');
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('no');
        };
    });