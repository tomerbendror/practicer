function singleQuestionPanel($timeout, msgBus) {
    return {
        scope: {
            question: "="
        },  // use a new isolated scope
        restrict: 'AE',
        replace: 'false',
        templateUrl: '/html/app/directives/singleQuestionPanel.html',
        controller: singleQuestionPanelCtrl,
        link: function ($scope, element) {
            $scope.element = $(element);
            $timeout(function(){onQuestionInfoLinked($scope.element, $scope, $timeout);}, 0);
        }
    };
}

function singleQuestionPanelCtrl($scope, $modal, langService,  msgBus) {

    $scope.collapse = true;
    $scope.useTTS = ($scope.question.questionType == 'DICTATION');
    $scope.possibleAnswers = $scope.question.answers;   // for case of multiple correct answers
    $scope.question.ttsStillRelevant = true;

    $scope.setQuestionStrStyle = function() {
        if ($scope.question.questionType == 'MATH') {
            $scope.questionTextlStyle = {direction: 'ltr'};
        } else {
            $scope.questionTextlStyle = {direction: langService.isHebrew($scope.question.questionStr) ? 'rtl' : 'ltr'};
        }
    };
    $scope.setQuestionStrStyle();

    $scope.shouldDisplayTTSButton = function() {
        var questionType = $scope.question.questionType;
        return (questionType == 'TRANSLATION' || questionType == 'DICTATION') &&
            $scope.question.ttsUrl && $scope.$parent.practice.useTTS && $scope.question.ttsStillRelevant;
    };
    $scope.displayTTSButton = $scope.shouldDisplayTTSButton();
    $scope.isSingleAnswer = $scope.question.questionType == 'MATH';

    $scope.getAnswersAsStr = function() {
        var ret = '';
        if ($scope.question.answers.length == 1) {
            return $scope.question.answers[0].answerStr;
        }

        for (var index = 0; index < $scope.question.answers.length; index++) {
            var currentAnswer = $scope.question.answers[index];
            if (index != 0) {
                ret += ' ';
            }
            ret += '●' + currentAnswer.answerStr;
        }
        return ret;
    };
    $scope.answersAsStr = $scope.getAnswersAsStr();

    $scope.refreshQuestion = function() {
        $scope.answersAsStr = $scope.getAnswersAsStr();
        $scope.displayTTSButton = $scope.shouldDisplayTTSButton();
        $scope.setQuestionStrStyle();
    };

    $scope.toggleCollapse = function() {
        if ($scope.collapse == false) { // need to close
            $scope.collapse = !$scope.collapse;
            $scope.$parent.setOpenQuestionElement();
            $scope.element.wrapkitPanel( "collapse", $scope.collapse);
        } else {    // need to open
            var currentOpenQuestionElement = $scope.$parent.getOpenQuestionElement();
            if (currentOpenQuestionElement) {
                currentOpenQuestionElement.wrapkitPanel("collapse", true);
            }
            $scope.element.wrapkitPanel("collapse", false);
            $scope.$parent.setOpenQuestionElement($scope.element);

            var questionType = $scope.question.questionType;
            if ($scope.question.id != -1 && $scope.shouldDisplayTTSButton()) {
                var audio = $scope.element.find('audio');
                if (audio && !audio.attr('src')) {
                    audio.attr('src', audio.attr('async-src'));
                }
            }
        }
    };

    $scope.editQuestion = function () {
        var modalInstance = $modal.open({
            templateUrl: '/js/app/parent/edit-question/editQuestionDialog.html',
            controller: 'editQuestionController',
            //size: 'lg',
            resolve: {
                'questionToEdit': function() { return angular.copy($scope.question); },
                'title': function() { return 'עריכת שאלה'; },
                'newQuestionMode': function() { return false; },
                'allQuestions': function() { return $scope.$parent.$parent.$parent.allQuestions; },
                'questionsType': function() { return $scope.question.questionType; }
            }
        });
        modalInstance.result.then(function (editedQuestion) {
            $scope.question.ttsStillRelevant = $scope.question.ttsStillRelevant && editedQuestion.questionStr.toLowerCase() == $scope.question.questionStr.toLowerCase();
            $scope.mergeQuestionData(editedQuestion);
            $scope.refreshQuestion();
        }, function () {
            console.log('editQuestionDialog dismissed at: ' + new Date());
        });
    };

    $scope.mergeQuestionData = function(editedQuestion) {
        if ($scope.question.id == editedQuestion.id) {  // just in case
            $scope.question.questionStr = editedQuestion.questionStr;
            $scope.question.answers = editedQuestion.answers;
            $scope.question.hint = editedQuestion.hint;
        }
    };

    $scope.deleteQuestion = function() {
        var msg = ($scope.question.id == -1) ? 'האם לבטל את הוספת השאלה?' : 'האם להסיר את השאלה?';
        if ($scope.question.id == -1) {
            msg = 'האם לבטל את הוספת השאלה?';
        } else {
            var questionStr = trimToLen($scope.question.questionStr, 30);
            msg = "האם להסיר את השאלה '" + questionStr +"'?";
        }
        bootbox.confirm(msg, function(userAnswer) {
            if (userAnswer) {
                msgBus.emitMsg('deleteQuestionMsg', $scope.question);
                $scope.$apply();
            }
        });
    };

    msgBus.onMsg('practiceEditReverted', function(event, practiceId) {
        $scope.question.ttsStillRelevant = true;
        $scope.refreshQuestion();
    }, $scope);

    $scope.scrollQuestionTop = function() {
        var scrollToValue;
        var scrollContainer;
        var expendedElement = $(".panel-expand");
        if (expendedElement.length) {   // the question panel is currently expended
            var expandedContainer = expendedElement;
            scrollContainer = expandedContainer;
            scrollToValue = expandedContainer.scrollTop() + $scope.element[0].position().top -2;
        } else {
            scrollContainer = $('html,body');
            scrollToValue = findPos($scope.element[0]) -TOP_HEADER_HEIGHT;
        }

        scrollContainer.animate({
            scrollTop: scrollToValue
        }, 250, 'swing');
    };

    msgBus.onMsg(QUESTIONS_TTS_URL_UPDATE_EVENT, function(event) {
        if ($scope.question.ttsUrl) {
            // todo - do it only if the tts is not relevant for this question
            $scope.displayTTSButton = $scope.shouldDisplayTTSButton();
            $scope.element.find("audio").attr("src", $scope.question.ttsUrl);
        }
    }, $scope);

}

function onQuestionInfoLinked(element, $scope) {
    var $panel = element.find(".panel.panel-default"),
        data = $panel.data(),
        options = {};

    options.collapse =  true;
    options.expand = false;
    options.color = 'none';
    // init
    $panel.wrapkitPanel(options);

    element.on("wrapkit.panel.collapse", function (e, isCollapse) {
        $scope.collapse = isCollapse;
    });
}