function childAllPracticesController($scope, dataService, msgBus) {
    msgBus.emitMsg('navigationEvent', 'כל התרגילים');

    msgBus.onMsg('searchPracticeMsg', function(event, searchString) {
        $scope.updatePracticesList(searchString);
    }, $scope);

    $scope.updatePracticesList = function(searchString) {
        if (jQuery.isEmptyObject(searchString) || searchString.trim() == '') {  // if no filter required
            $scope.groupIdToPracticesMap = $scope.groupIdToPracticesMapOriginal;
            return;
        }

        $scope.filteredPractices = {};
        for (var key in $scope.groupIdToPracticesMapOriginal) {
            if ($scope.groupIdToPracticesMapOriginal.hasOwnProperty(key)) {
                var practicesAry = $scope.groupIdToPracticesMapOriginal[key];
                for (var t=0; t<practicesAry.length; t++) {
                    var practice = practicesAry[t];
                    if (practice.name.toLowerCase().indexOf(searchString.toLowerCase()) > -1) {
                        var filteredGroupPractices = $scope.filteredPractices[key];
                        if( typeof filteredGroupPractices === 'undefined' || filteredGroupPractices === null ){
                            filteredGroupPractices = [];
                            $scope.filteredPractices[key] = filteredGroupPractices;
                        }
                        filteredGroupPractices.push(practice);
                    }
                }
            }
        }
        $scope.groupIdToPracticesMap = $scope.filteredPractices;
    };

    dataService.getChildSummary()
        .then(function (result) {
            $scope.practicesSummary = result.practicesSummary;

            var practices = result.practicesSummary;
            $scope.groupIdToPracticesMapOriginal = {};

            for (var i=0; i<practices.length; i++) {
                var practice = practices[i];
                var groupPractices = $scope.groupIdToPracticesMapOriginal[practice.groupId];
                if( typeof groupPractices === 'undefined' || groupPractices === null ){
                    groupPractices = [];
                    $scope.groupIdToPracticesMapOriginal[practice.groupId] = groupPractices;
                }

                practice.displayAsDictationBtn = false;
                if (practice.questionsType == 'DICTATION') {
                    practice.questionsTypeStr = 'הכתבה';
                } else if (practice.questionsType == 'TRANSLATION') {
                    practice.questionsTypeStr = 'תרגום';
                    practice.displayAsDictationBtn = true;
                } else if (practice.questionsType == 'MATH') {
                    practice.questionsTypeStr = 'חשבון';
                }

                groupPractices.push(practice);
            }
            $scope.groupIdToPracticesMap = $scope.groupIdToPracticesMapOriginal;
        }, function () {
            toastr.error('אירעה שגיאת רשת, אנא נסו מאוחר יותר');//todo
        });
}