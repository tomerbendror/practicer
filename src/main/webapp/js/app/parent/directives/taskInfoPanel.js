function taskInfoPanel($timeout) {
    return {
        scope: {
            task: "="
        },  // use a new isolated scope
        restrict: 'AE',
        replace: 'false',
        templateUrl: '/html/app/directives/singleQuestionPanel.html',
        controller: taskInfoPanelCtrl,
        link: function ($scope, element) {
            $scope.element = $(element);
            $timeout(function(){onTaskInfoLinked($scope.element, $scope);}, 0);
        }
    };
}

function taskInfoPanelCtrl($scope) {
    $scope.collapse = true;
    $scope.expand = false;

    $scope.toggleCollapse = function() {
        if ($scope.collapse == false) {
            $scope.collapse = !$scope.collapse;
            $scope.element.find(".panel.panel-default").wrapkitPanel( "collapse", $scope.collapse);
            return;
        }
        $('#all-tasks-container').find('.panel.panel-default').each(function() {
            $( this ).wrapkitPanel( "collapse", true);
        });
        $scope.element.find(".panel.panel-default").wrapkitPanel( "collapse", !$scope.collapse);
    };

    $scope.toggleExpand = function() {
        if ($scope.collapse) {
            $scope.toggleCollapse();
        }
        $scope.element.find(".panel.panel-default").wrapkitPanel( "expand", !$scope.expand);
    }
}

function onTaskInfoLinked(element, $scope) {
    var $panel = element.find(".panel.panel-default"),
        data = $panel.data(),
        options = {};

    options.collapse =  true;
    options.expand = false;
    options.color = 'none';
    // init
    $panel.wrapkitPanel(options);

    $panel.on("wrapkit.panel.collapse", function (e, isCollapse) {
        $scope.collapse = isCollapse;
    });

    $panel.on("wrapkit.panel.expand", function (e, isExpand) {
        $scope.expand = isExpand;
    });
}