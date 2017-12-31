function parentGroupsCtrl($scope, parentDataService) {
    parentDataService.getAllGroups()
        .then(function (groups) {
            $scope.groups = groups;
        });

    parentDataService.getAllChilds()
        .then(function (childs) {
            $scope.childs = childs;
        });

    $scope.clearNewGroupData = function() {
        $scope.newGroupInvite = [];
        $scope.group = {name: '', description: ''};
    };

    $scope.enableAddGroupBtn = true;
    $scope.enableCreateGroupBtn = true;
    $scope.clearNewGroupData();

    $scope.addGroup = function() {
        $scope.enableAddGroupBtn = false;
        $("#newGroupPanel").slideDown("slow", function() {
            $('#newGroupNameInput').focus();
        });
    };

    $scope.createGroup = function() {
        if (!$scope.validateNewGroup()) {
            return;
        }
        $scope.enableCreateGroupBtn = false;
        $scope.group.groupInvitations = $scope.newGroupInvite;
        parentDataService.createGroup($scope.group)
            .then(function (createdGroup) {
                $scope.enableCreateGroupBtn = true;
                $scope.groups.unshift(createdGroup);
                toastr.success('הקבוצה נוצרה בהצלחה');
                $scope.cancelAddGroup();
                $scope.clearNewGroupData();
            }, function() {
                $scope.enableCreateGroupBtn = true;
            });
    };

    $scope.validateNewGroup = function() {
        if ($.trim($scope.group.name) === '') {
            $scope.error('יש להזין שם לקבוצה');
            return false;
        }
        return true;
    };

    $scope.cancelAddGroup = function() {
        $scope.enableAddGroupBtn = true;
        $("#newGroupPanel").slideUp("slow");
    };

    $scope.error = function(msg) {
        $scope.errorMsg = msg;
        return false;
    };

    setActiveMenu('#groups-btn');
}