function childPracticeController($scope, $routeParams, $timeout, $mdDialog, $location, dataService, langService, audio1, audio2) {
    $scope.practiceId = $routeParams.practiceId;
    $scope.displayStartFab = false;
    $scope.state = 'PREPARING'; // [PREPARING, READY, PRACTICE]
    $scope.currentQuestionIndex = 0;    // index used to get the next question in case of non-random order
    $scope.notDisplayedQuestions = [];  // for random order, we use this array to hold the question we didn't displayed yet, in order not to display a question twice before all questions have been displayed
    $scope.caption = 'טוען תרגול...';
    $scope.isChrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;

    $scope.useAsDictation = $location.search().dictation;
    $scope.$on("$destroy", function() {
        $location.search({});
    });

    $scope.userAnswer = '';
    $scope.userNumberAnswer = '';

    dataService.getChildProperties().then(function (data) {
        $scope.userAvatar = data.userAvatar;
    });

    dataService.getPractice($scope.practiceId)
        .then(function (result) {
            $scope.initPractice(result.responseObject);
            $scope.displayStartFab = true;
        });

    $scope.startPracticeClicked = function() {
        $scope.state = "PRACTICE";
        $scope.startPractice($scope.practice);  // I would like this line to be on the delay scope, but chrome mobile is not letting me play the audio only if the user trigger any click
        $timeout(function() {
            //$scope.startPractice($scope.practice);
            $scope.textFieldElement.focus();
        }, 600);
    };

    $scope.answerSubmit = function(event) {
        var userAnswerStr = $scope.getUserAnswerStr().trim();
        if (event.keyCode != 13 || userAnswerStr == '') {
            return;
        }

        if ($scope.isCorrectAnswer()) {
            $scope.practiceScoreDisplay = Math.round($scope.practiceScore += $scope.singleQuestionScore);
            $scope.toNextQuestionOrFinish(true);
        } else {
            $timeout(function() {playWrongSound();}, 200);
            $timeout(function() {
                $scope.wrongAnswers.push(userAnswerStr);
                if ($scope.wrongAnswers.length >= $scope.practice.maxMistakesNum) {  // >= is just to be on the safe side
                    $scope.toNextQuestionOrFinish(false);
                    return;
                }
                $scope.replayTTS();
            }, 1000);
        }
        $scope.userAnswer = '';
        $scope.userNumberAnswer = '';
    };

    $scope.toNextQuestionOrFinish = function(success) {
        $scope.addQuestionResult(success);

        if ($scope.practice.questionsPerPractice == $scope.currentQuestionNum) {    // practice finished
            $scope.textFieldElement.blur(); // close the keyboard for mobile
            $scope.sendPracticeResult();
            $scope.showConfirm(event);
            return;
        }
        $scope.currentQuestionNum++;
        $scope.updateProgressBar();
        $scope.loadQuestion($scope.nextQuestion);
        $scope.userAnswer = '';
        $scope.userNumberAnswer = '';
    };

    $scope.showConfirm = function(event) {
        var confirm = $mdDialog.confirm()
            .title('התרגיל הסתיים')
            .content('ציון ' + Math.round($scope.practiceScore))
            .targetEvent(event)
            .cancel('תרגול נוסף')
            .ok('סיום');
        $mdDialog.show(confirm).then(function() {
            $scope.onBack();
        }, function() {
            $scope.startOver();
        });
    };

    $scope.isCorrectAnswer = function() {
        var correctAnswers = $scope.currentQuestion.answers;

        var lowerCase = $scope.getUserAnswerStr().trim().toLowerCase() ;
        if ($scope.useAsDictation) {
            return $scope.currentQuestion.questionStr.trim().toLowerCase() == lowerCase;
        }
        for (var i=0; i<correctAnswers.length; i++) {
            var correctAnswer = correctAnswers[i].answerStr;
            correctAnswer = stripVowels(correctAnswer);
            if (lowerCase == correctAnswer.trim().toLowerCase()) {
                return true;
            }
        }
        return false;
    };

    $scope.getUserAnswerStr = function() {
        return $scope.practice.questionsType == 'MATH' ? "" + $scope.userNumberAnswer : $scope.userAnswer;
    };

    $scope.getNextQuestion = function() {
        if ($scope.practice.randomOrder) {
            if ($scope.notDisplayedQuestions.length == 0) {  // we finish display all the questions, need to load again all questions
                for (var i=0; i<$scope.practice.questions.length; i++) {
                    $scope.notDisplayedQuestions[i] = $scope.practice.questions[i];
                }
            }
            var randomQuestion = $scope.notDisplayedQuestions[$scope.randomInt(0, $scope.notDisplayedQuestions.length - 1)];
            $scope.removeElementFromCollectionById($scope.notDisplayedQuestions, randomQuestion);
            return randomQuestion;
        } else {
            if ($scope.currentQuestionIndex == $scope.practice.questions.length) {
                $scope.currentQuestionIndex = 0;
            }
            var ret = $scope.practice.questions[$scope.currentQuestionIndex];
            $scope.currentQuestionIndex++;
            return ret;
        }
    };

    $scope.removeElementFromCollectionById = function(collection, elementToRemove) {
        for (var i=0; i<collection.length; i++) {
            var element = collection[i];
            if (element.id == elementToRemove.id) {
                collection.splice(i, 1);
                return;
            }
        }
    };

    $scope.randomInt = function(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    };

    $scope.startPractice = function() {
        $scope.loadQuestion($scope.currentQuestion);
    };

    $scope.replayTTS = function() {
        if ($scope.practice.useTTS) {
            $scope.currentAudio.play();
            $scope.textFieldElement.focus();
        }
    };

    $scope.showMeTheAnswer = function() {
        var answersStr = '';
        if ($scope.useAsDictation) {
            answersStr = $scope.currentQuestion.questionStr;
        } else {
            for (var i=0; i<$scope.currentQuestion.answers.length; i++) {
                if (i != 0) {
                    answersStr += ','
                }
                answersStr += $scope.currentQuestion.answers[i].answerStr;
            }
        }

        var alert = $mdDialog.alert()
            .title(answersStr)
            .ok('המשך');

        $scope.questionSkipped = true;

        $mdDialog
            .show( alert )
            .finally(function() {
                $scope.toNextQuestionOrFinish(false);
                $scope.textFieldElement.focus();
            });
    };

    $scope.loadQuestion = function(questionToLoad) {
        $scope.questionSkipped = false;
        $scope.wrongAnswers = [];
        $scope.questionStartTime = new Date().getTime();
        $scope.currentQuestion = questionToLoad;
        $scope.currentQuestionStr = $scope.getQuestionStr($scope.currentQuestion);
        $scope.questionStyle = langService.isHebrew($scope.currentQuestion.questionStr) ? {direction: 'rlt'} : {direction: 'ltr'};
        $scope.nextQuestion = $scope.getNextQuestion();
        if ($scope.practice.useTTS) {
            $scope.currentAudio = $scope.getAudio($scope.currentQuestionNum);
            $scope.currentAudio.play();  // play the current question
            $scope.getAudio($scope.currentQuestionNum + 1).load($scope.nextQuestion.ttsUrl);  // and load the next one
        }
    };

    $scope.getQuestionStr = function(question) {
        if ($scope.practice.questionsType == 'DICTATION' || $scope.useAsDictation) {
            var specialChars = '- .,?';
            var retVal = '';
            var questionStr = stripVowels(question.questionStr);
            for (var i=0; i<questionStr.length; i++) {
                var c = question.questionStr[i];
                if (specialChars.indexOf(c) != -1) {
                    retVal += c;
                } else {
                    retVal += "_";
                }
            }
            return retVal;
        }
        return question.questionStr;
    };

    $scope.getAudio = function(questionNum) {
        return (questionNum%2 == 0) ? audio1 : audio2;
    };

    $scope.playTtsIfNeeded = function() {
        if ($scope.practice.useTTS) {
            $scope.audioSrc = $scope.currentQuestion.ttsUrl;
            audio1.play($scope.audioSrc)
        }
    };

    $scope.initPractice = function(practice) {
        $scope.practice = practice;
        $scope.caption = practice.name;
        $scope.displayReplayBtn = $scope.practice.useTTS;
        $scope.questionsInPractice = $scope.practice.questionsPerPractice;
        $scope.currentQuestionNum = 1;
        $scope.practiceScore = 0;
        $scope.practiceScoreDisplay = 0;    // for the display we need to round the score
        $scope.updateProgressBar();
        $scope.singleQuestionScore = 100/$scope.questionsInPractice;

        $scope.currentQuestion = $scope.getNextQuestion();
        $scope.getAudio($scope.currentQuestionNum).load($scope.currentQuestion.ttsUrl);
        $timeout(function() {
            $scope.textFieldElement = $scope.practice.questionsType == 'MATH' ? ($scope.isChrome ? $('#chrome-answer-number-input') : $('#answer-number-input')) : $('#answer-text-input');
        }, 0);

        $scope.questionResults = [];

        //$scope.textFieldElement = $scope.practice.questionsType == 'MATH' ? $('#answer-number-input') : $('#answer-text-input');
    };

    $scope.startOver = function() {
        $scope.initPractice($scope.practice);
        $scope.startPracticeClicked();
    };

    $scope.updateProgressBar = function() {
        $scope.progressValue = (100/ $scope.questionsInPractice) * $scope.currentQuestionNum;
    };

    $scope.onBack = function() {
        $location.path('/all-practices')
    };

    $scope.sendPracticeResult = function() {
        var practiceResult = {
            practiceId: $scope.practice.id,
            score: $scope.practiceScoreDisplay,
            questionResults: $scope.questionResults
        };
        console.log(practiceResult);
        dataService.sendPracticeResult(practiceResult);
    };

    $scope.addQuestionResult = function(success) {
        var currentTime = new Date().getTime();
        var questionTimeSec = (currentTime - $scope.questionStartTime) / 1000;

        var wrongAnswersStr = '';
        for(var i=0; i<$scope.wrongAnswers.length; i++) {
            if (i != 0) {
                wrongAnswersStr += ';';
            }
            wrongAnswersStr += $scope.wrongAnswers[i];
        }

        $scope.questionResults.push({
            questionStr: $scope.currentQuestion.questionStr,
            questionTimeSecond: questionTimeSec,
            wrongAnswersStr: wrongAnswersStr,
            questionSkipped: $scope.questionSkipped,
            success: success
        });
    };

    // animation for the open of this page
    $timeout(function() {
        $scope.displayMainDiv = true;
    }, 0);
}