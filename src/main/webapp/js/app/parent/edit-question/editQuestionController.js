angular.module('parentApp').controller('editQuestionController', function modalController ($scope, $http, $modalInstance, langService,
                                                                                           questionToEdit, title, newQuestionMode, questionsType, allQuestions) {
    $scope.title = title;
    $scope.newQuestionMode = newQuestionMode;
    $scope.questionsType = questionsType;
    $scope.allQuestions = allQuestions;
    $scope.errorMsg = '';

    $scope.newQuestion = function() {
        var newQuestion = {
            id: -1,
            questionStr: '',
            ttsStillRelevant: false,
            questionType: $scope.questionsType
        };

        if ($scope.questionsType == 'MATH') {
            newQuestion.answers = [];
            newQuestion.answers[0] = {answerStr: ''};
        }
        return newQuestion;
    };

    if ($scope.newQuestionMode) {
        $scope.newQuestions = [];   // used only in new practice mode
        $scope.question = $scope.newQuestion();
    } else {
        $scope.question = questionToEdit;
    }
    $scope.prevQuestionStr = $scope.question.questionStr;   // enable to detect if the value was change by compare to prev value - for know if need to call translation again
    $scope.originalQuestionStr = $scope.question.questionStr;   // enable to detect if we need to check if this question already exist among practice.questions
    $scope.isSingleAnswer = $scope.question.questionType == 'MATH';

    $scope.done = function () {
        // special handling for case where user click 'done and add more' instead of just 'done', we allow him to quit if the question data is empty
        if ($scope.newQuestionMode && $scope.newQuestions.length > 0 && isBlank($scope.question.questionStr)) {
            $modalInstance.close($scope.newQuestions);
            return;
        }

        if ($scope.validateQuestion()) {
            if ($scope.newQuestionMode) {
                $scope.newQuestions.push($scope.question);
                $modalInstance.close($scope.newQuestions);
            } else {
                $modalInstance.close($scope.question);
            }
        }
    };

    $scope.addOneMoreQuestion = function () {
        if ($scope.validateQuestion()) {
            if ($scope.newQuestionMode) {  // just in case - for update scenario this button should not be displayed
                $scope.newQuestions.push($scope.question);
                $scope.question = $scope.newQuestion();
                jQuery('#edit-question-dialog-question_str').focus();
            }
        }
    };

    $scope.cancel = function () {
        if ($scope.newQuestions && $scope.newQuestions.length > 0) {
            var msg = ($scope.newQuestions.length == 1 ? "ישנה שאלה לא שמורה" : "ישנם " + $scope.newQuestions.length + " שאלות לא שמורות") + ", האם לשמור תחילה?";
            bootbox.confirm({
                message: msg,
                buttons: {
                    'cancel': {
                        label: 'לא, מחק',
                        className: 'btn-default'
                    },
                    'confirm': {
                        label: 'כן, שמור',
                        className: 'btn-primary'
                    }
                },
                callback: function(result) {
                    if (result) {
                        $scope.done();
                    } else {
                        $modalInstance.dismiss('cancel');
                        console.log('cancel');
                    }
                }
            });
        } else {
            $modalInstance.dismiss('cancel');
            console.log('cancel');
        }
    };

    $scope.validateQuestion = function() {
        var questionStr = '' + $scope.question.questionStr;
        if (isBlank(questionStr)) {
            return $scope.showError("יש להזין ערך בשדה 'שאלה'");
        }

        if ($scope.question.answers.length < 1) {
            return $scope.showError("יש להזין לפחות תשובה אחת");
        }

        if (isBlank('' + $scope.question.answers[0].answerStr)) {  // math answer validation
            return $scope.showError("יש להזין לפחות תשובה אחת");
        }

        if ($scope.newQuestionMode || $scope.originalQuestionStr.trim().toLocaleLowerCase() != questionStr.trim().toLowerCase()) {
            var allQuestionsTmp = $scope.allQuestions;
            if ($scope.newQuestionMode) {
                allQuestionsTmp = allQuestionsTmp.concat($scope.newQuestions);
            }
            for (var i = 0; i < allQuestionsTmp.length; i++) {
                var currentQuestion = allQuestionsTmp[i];
                if (('' + currentQuestion.questionStr).trim().toLowerCase() == questionStr.trim().toLowerCase()) {
                    return $scope.showError("השאלה '" + questionStr + "' כבר קיימת בתרגיל");
                }
            }
        }

        $('#edit-question-error-panel').slideUp();
        return true;
    };

    $scope.showError = function(errorMsg) {
        $scope.errorMsg = errorMsg;
        $('#edit-question-error-panel').slideDown();
        return false;
    };

    $scope.questionFieldKeyPress = function(keyEvent) {
        if (keyEvent.which === 13) {
            var inputs = jQuery('.question-modal-content').find(':input').not(':button');
            var nextInput = inputs.get(inputs.index(document.activeElement) + 1);
            if (nextInput) {
                nextInput.focus();
            }
        }
    };

    $scope.answerExist = function(answerToCheck) {
        var answers = $scope.question.answers;
        for (var i = 0; i < answers.length; i++) {
            var answer = answers[i];
            if (answerToCheck.trim() == answer.answerStr) {
                return true;
            }
        }
        return false;
    };

    $scope.stringIsInLang = function(str, charSet) {
        var index;
        for (index = str.length - 1; index >= 0; --index) {
            if (charSet.indexOf(str.substring(index, index + 1)) < 0) {
                return false;
            }
        }
        return true;
    };

    $scope.getQuestionStrPlaceHolder = function() {
        var questionType = $scope.question.questionType;
        if (questionType == 'MIX') {
            return 'לדוגמא: 6*9 או Home';
        } else if (questionType == 'MATH') {
            return 'לדוגמא: 6*9';
        } else if (questionType == 'DICTATION' || questionType == 'TRANSLATION') {
            return 'לדוגמא: Home';
        } else if (questionType == 'IMAGE') {
            return 'לדוגמא: איזה מהבאים הינו משולש ישר זווית?';
        }
        return questionType == 'IMAGE' || questionType == 'MIX';
    };
    $scope.questionStrPlaceHolder = $scope.getQuestionStrPlaceHolder();

    $scope.onQuestionBlur = function() {
        var questionStr = $scope.question.questionStr;
        if ($.trim(questionStr) === '' || ($scope.prevQuestionStr && (questionStr.trim() == $scope.prevQuestionStr.trim()))) {
            return;
        }

        $scope.prevQuestionStr = questionStr;
        if ($scope.question.questionType == 'MATH') {
            questionStr = questionStr.replace(/x/gi, "*").replace(/:/gi, "/");
            try {
                $scope.question.answers[0].answerStr = eval(questionStr);
            } catch (e) {
            }
        } else if ($scope.question.questionType == 'DICTATION') {
            var answers = $scope.question.answers;
            if (!$scope.answerExist(questionStr)) {
                answers[answers.length] = {answerStr: questionStr};
            }
        } else if ($scope.question.questionType == 'TRANSLATION') {
            var toTranslate;
            if ($scope.stringIsInLang(questionStr, langService.ENGLISH)) {
                toTranslate = {
                    'text': questionStr,
                    'sourceLanguage' : 'EN',
                    'targetLanguage' : 'HE'
                };
            } else if ($scope.stringIsInLang(questionStr, langService.HEBREW)) {
                toTranslate = {
                    'text': questionStr,
                    'sourceLanguage' : 'HE',
                    'targetLanguage' : 'EN'
                };
            }
            if (toTranslate) {
                $http.post('/translate', toTranslate).then(function (result) {
                    // make sure the translation match the source text, in some case the user may click 'add another question' before the translation get back from the server
                    if (result.data.text == $scope.question.questionStr) {
                        var answers = $scope.question.answers;
                        if (result.data.translations.length > 0) {
                            var translation = result.data.translations[0];
                            var original = translation.trim();
                            if (!$scope.answerExist(original)) {
                                answers[answers.length] = {answerStr: translation};
                            }
                        }
                    }
                });
            }
        }
    };
});