angular.module('parentApp').controller('selectLangDialogController', function modalController ($scope, $modalInstance, title) {
    $scope.title = title;
    $scope.lang = 'EN';

    $scope.done = function () {
        $modalInstance.close($scope.lang);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});