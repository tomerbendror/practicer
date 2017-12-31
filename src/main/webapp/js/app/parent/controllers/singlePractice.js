function singlePractice($scope, $routeParams, $timeout, $modal, $location, $window, parentDataService, contextService, msgBus, langService, practiceOperationService, yesNoModal) {
    $scope.isWaitingForServer = false;
    $scope.lazyLoad = false;
    $scope.newPractice = function(practiceType) {
        parentDataService.getAllChilds()
            .then(function (childs) {
                $scope.updatePracticeChildSharedData(childs);
            });

        parentDataService.getAllGroups()
            .then(function (groups) {
                $scope.updatePracticeGroupSharedData(groups);
            });

        var newPractice = {
            id: -1,
            description: 'תרגול חדש',
            questionsType: practiceType,
            maxSecondsForQuestion: 0,
            maxMistakesNum: 3,
            questionCount: 0,
            testMode: false,
            randomOrder: true,
            enableShowMeTheAnswer: true,
            questions: [],
            useTTS: practiceType != 'MATH',
            questionsPerPractice: 20,
            practiceSharedChildData: [],//todo
            practiceSharedGroupData: []//todo
        };

        if (practiceType == 'DICTATION') {
            newPractice.name = 'הכתבה';
        } else if (practiceType == 'TRANSLATION') {
            newPractice.name = 'תרגום מילים';
        } else if (practiceType == 'MATH') {
            newPractice.name = 'תרגול חשבון';
        } else {    // should not happens
            newPractice.name = 'תרגול חדש';
        }
        return newPractice;
    };

    $scope.updatePracticeChildSharedData = function(childs) {
        var childShareData = $scope.practice.practiceSharedChildData;
        for(var i=0; i<childs.length; i++) {
            var child = childs[i];
            childShareData[childShareData.length] = {
                childId: child.id,
                childName: child.firstName,
                isShare: false,
                gender: child.gender
            };

        }
    };

    $scope.updatePracticeGroupSharedData = function(groups) {
        var groupsShareData = $scope.practice.practiceSharedGroupData;
        for(var i=0; i<groups.length; i++) {
            var group = groups[i];
            groupsShareData[groupsShareData.length] = {
                groupId: group.id,
                isShare: false,
                groupName: group.name
            };
        }
    };

    $scope.validateScreenCalculation = function(delay) {
        if (delay) {
            $timeout(function() {onResize();}, delay);
        } else {
            onResize();
        }
    };

    $scope.onShareMediaClicked = function(element) {
        if (!$scope.practiceViewMode) {
            element.isShare = !element.isShare;
        }
    };

    $scope.initPractice = function(practice) {
        $scope.practice = practice;
        $scope.allQuestions = practice.questions;
        $scope.currentChunck = 3;
        var firstChunck = $scope.currentChunck * $scope.scrollChanck;
        $scope.lazyLoad = $scope.allQuestions.length > firstChunck;
        var chunckQuestions = $scope.allQuestions.slice(0, firstChunck);
        practice.questions = [];
        for (var index=0; index<chunckQuestions.length; index++) {   // implementing deep copy of the questions in chunckQuestions
            var currentQuestion = chunckQuestions[index];
            practice.questions.push(angular.copy(currentQuestion));
        }
        $scope.userHasEditPermissions = $scope.practice.id == -1 || $scope.practice.creatorId == contextService.getUserId();
        $scope.practiceDescription = getPracticeTypeStr($scope.practice.questionsType);
        $scope.practice.deletedQuestionsIds = [];
    };

    $scope.scrollChanck = 10;
    if (isPracticeType($routeParams.param)) {   // create new mode
        $scope.createMode = true;
        $scope.useInfiniteScroll = false;
        $scope.saveBtnCaption = 'יצירה';
        $scope.initPractice($scope.newPractice($routeParams.param));

        $scope.practiceViewMode = false;
        $scope.practiceContainerClass = 'edit-mode';
        $scope.validateScreenCalculation(600);

        //$scope.validateScreenCalculation(50); //todo invoke it after get back from the server with the share data
    } else {                                    // update existing mode
        $scope.createMode = false;
        $scope.practiceId = $routeParams.param;
        $scope.practiceViewMode = true;
        $scope.useInfiniteScroll = true;
        $scope.useInfiniteScroll = true;
        $scope.currentChunck = 0;
        $scope.lazyLoadFinished = false;
        $scope.saveBtnCaption = 'שמירה';

        $timeout(function() {
            parentDataService.getPractice($scope.practiceId)
                .then(function (practice) {
                    $scope.initPractice(practice);
                    $scope.validateScreenCalculation(50);
                }, function () {
                    onResize(); // stop the busy animation in case of error
                    $location.path('/exercises');
                });
        }, 0);
    }

    $scope.tabs = [{active: true}, {active: false}, {active: false}];
    $scope.tabs['GENERAL_TAB'] = {active: true};
    $scope.tabs['QUESTIONS_TAB'] = {active: false};
    $scope.tabs['SHARE_TAB'] = {active: false};

    $scope.nextPage = function() {
        if ($scope.practice && !$scope.lazyLoadFinished) {
            if (this.busy) return;
            this.busy = true;
            var fromIndex = $scope.currentChunck * $scope.scrollChanck;
            $scope.currentChunck++;
            var toIndex = $scope.currentChunck * $scope.scrollChanck;
            var newChunckArray = $scope.allQuestions.slice(fromIndex, toIndex);
            if (newChunckArray.length < $scope.scrollChanck) {
                $scope.lazyLoadFinished = true;
                $scope.lazyLoad = false;
            }
            for (var index=0; index<newChunckArray.length; index++) {   // implementing deep copy of the questions in newChunckArray
                var currentQuestion = newChunckArray[index];
                $scope.practice.questions.push(angular.copy(currentQuestion));
            }
            this.busy = false;
        }
    };

    $scope.loadAllQuestions = function() {
        var fromIndex = $scope.currentChunck * $scope.scrollChanck;
        var newChunckArray = $scope.allQuestions.slice(fromIndex, $scope.allQuestions.length);
        for (var index=0; index<newChunckArray.length; index++) {   // implementing deep copy of the questions in newChunckArray
            var currentQuestion = newChunckArray[index];
            $scope.practice.questions.push(angular.copy(currentQuestion));
        }
        $scope.lazyLoadFinished = true;
        $scope.lazyLoad = false;
    };

    // holding the current open question element (if any), we will close this element when need to open other question
    $scope.setOpenQuestionElement = function(openQuestionElement) {
        $scope.openQuestionElement = openQuestionElement;
    };

    $scope.getOpenQuestionElement = function() {
        return $scope.openQuestionElement;
    };

    $(window).on("resize.singlePracticeResizeListener",(function() {
        setTimeout(function() {
            onResize();
        }, 0);
    }));

    $scope.$on('$destroy', function() {
        $(window).off("resize.singlePracticeResizeListener");
    });

    $scope.createQuestion = function() {
        $scope.setActiveTab('QUESTIONS_TAB');
        var modalInstance = $modal.open({
            templateUrl: '/js/app/parent/edit-question/editQuestionDialog.html',
            controller: 'editQuestionController',
            keyboard: false,
            backdrop: 'static',
            //size: 'lg',
            resolve: {
                'questionToEdit': function() { return {}; },    // this is mandatory field because of this dialog is also for edit single question
                'title': function() { return 'שאלה חדשה'; },
                'newQuestionMode': function() { return true; },
                'allQuestions': function() { return $scope.allQuestions; },
                'questionsType': function() { return $scope.practice.questionsType; }
            }
        });
        modalInstance.result.then(function (newQuestionAry) {
            for(var i=newQuestionAry.length-1; i>=0; i--) {
                var newQuestion = newQuestionAry[i];
                newQuestion.ttsStillRelevant = false;
                $scope.practice.questions.unshift(newQuestion);
            }
            $scope.validateScreenCalculation(50);
            $("html, body").animate({ scrollTop: 0 }, "slow");
        }, function() {$scope.validateScreenCalculation(50);});
    };

    $scope.editPractice = function() {
        $scope.practiceViewMode = false;
        $scope.practiceContainerClass = 'edit-mode';
        $scope.practiceBeforeEdit = angular.copy($scope.practice);
        $scope.validateScreenCalculation(600);
    };

    $scope.deletePractice = function() {
        practiceOperationService.deletePractice($scope.practice).then(function() {
            $location.path('/exercises');
        });
    };

    $scope.copyQuestionsToExistPractice = function() {
        practiceOperationService.copyQuestionsToExistPractice($scope.practice);
    };

    $scope.duplicatePractice = function() {
        practiceOperationService.duplicatePractice($scope.practice);
    };

    $scope.hasUniversalQuestionLanguage = function(practice) {
        for(var i=0; i < practice.questions.length; i++) {
            var question = practice.questions[i];
            if (langService.isUniversal(question.questionStr)) {
                return true;
            }
        }
        return false;
    };

    $scope.createOrUpdatePractice = function() {
        if (!$scope.validatePractice()) {
            return;
        }
        $scope.createOrUpdatePracticeWithoutShareValidation(true);
    };

    $scope.createOrUpdatePracticeWithoutShareValidation = function(validate) {
        if (validate && $scope.hasAnyShare() == false) {
            yesNoModal.open("יצירת תרגיל", 'התרגיל לא שותף עם אף קבוצה או ילד, האם להמשיך?', function(answer) {
                if (answer == 'yes') {
                    $scope.createOrUpdatePracticeWithoutShareValidation(false);
                }
            });
            return;
        }

        var practice = $scope.practice;
        if ($scope.practice.useTTS && $scope.hasUniversalQuestionLanguage(practice) && !practice.ttsLang) {
            var modalInstance = $modal.open({
                templateUrl: '/js/app/parent/select-lang-dialog/selectLangDialog.html',
                controller: 'selectLangDialogController',
                resolve: {
                    'title': function() { return 'בחירת שפת הקראה'; }
                }
            });
            modalInstance.result.then(function (ttsLang) {
                practice.ttsLang = ttsLang;
                $scope.createOrUpdatePracticeWithoutShareValidation(false);
            });
            return;
        }
        $scope.isWaitingForServer = true;
        if (practice.id < 0) {  // create new practice
            parentDataService.createPractice(practice)
                .then(function (createdPractice) {
                    $scope.initPractice(createdPractice);
                    $scope.practiceContainerClass = '';
                    $scope.practiceViewMode = true;
                    $scope.validateScreenCalculation(50);
                    $scope.isWaitingForServer = false;
                    $scope.saveBtnCaption = 'שמירה';
                    if ($scope.fillTtsData(createdPractice)) {
                        toastr.success('התרגיל נוצר בהצלחה, ממתין לייצור קבצי קול...');//todo i18n
                    } else {
                        toastr.success('התרגיל נוצר בהצלחה');//todo i18n
                    }
                    $location.path('/practice/' + createdPractice.id, false);
                }, function() {
                    $scope.isWaitingForServer = false;
                });
        } else {    // update existing practice
            practice.deletedQuestionsIds = $scope.practice.deletedQuestionsIds;
            parentDataService.updatePractice(practice)
                .then(function (updatedPractice) {
                    $scope.initPractice(updatedPractice);
                    $scope.practiceContainerClass = '';
                    $scope.practiceViewMode = true;
                    $scope.validateScreenCalculation(600);
                    $scope.isWaitingForServer = false;
                    if ($scope.fillTtsData(updatedPractice)) {
                        toastr.success('התרגיל עודכן בהצלחה, ממתין לייצור קבצי קול...');//todo i18n
                    } else {
                        toastr.success('התרגיל עודכן בהצלחה');//todo i18n
                    }
                }, function() {
                    $scope.isWaitingForServer = false;
                });
        }
    };

    $scope.fillTtsData = function(practice) {
        if (practice.ttsGenerationState == 'IN_PROGRESS' && practice.useTTS) {
            $scope.startPollingForTtsCompletion();
            return true;
        }
        return false;
    };

    $scope.startPollingForTtsCompletion = function() {
        parentDataService.getTtsGenerationState($scope.practice.id)
            .then(function (state) {
                if (state.summaryState == 'IN_PROGRESS') {
                    $timeout(function() {
                        $scope.startPollingForTtsCompletion();
                    }, 1000);
                } else {
                    var questions = $scope.practice.questions;
                    for (var i=0; i<questions.length; i++) {
                        var question = questions[i];
                        var ttsUrl = state.questionIdToTtsUrl[question.id];
                        if (ttsUrl) {
                            question.ttsUrl = ttsUrl;
                        }
                    }
                    msgBus.emitMsg(QUESTIONS_TTS_URL_UPDATE_EVENT);
                    toastr.success('תהליך יצירת קבצי הקול הסתיים בהצלחה');//todo i18n
                }
            });
    };

    $scope.hasAnyShare = function() {
        var practice = $scope.practice;
        var shared = false;
        var childs = practice.practiceSharedChildData;
        for (var i = 0; i < childs.length; i++) {
            if (childs[i].isShare) {
                shared = true;
                break;
            }
        }

        if (!shared) {
            var groups = practice.practiceSharedGroupData;
            for (var j = 0; j < groups.length; j++) {
                if (groups[j].isShare) {
                    shared = true;
                    break;
                }
            }
        }

        if (!shared) {
            $scope.setActiveTab('SHARE_TAB');
        }
        return shared;
    };

    $scope.validatePractice = function() {//todo i18n
        var practice = $scope.practice;
        if ($.trim(practice.name) === '') {
            return $scope.validationError('יש להזין את שם התרגיל', 'GENERAL_TAB');
        }

        if ($.trim(practice.questionsPerPractice) === '') {
            return $scope.validationError('יש להזין את מספר השאלות בתרגיל', 'GENERAL_TAB');
        }

        if (isPositiveNumeric(practice.questionsPerPractice) == false) {
            return $scope.validationError('יש להזין מספר חיובי בשדה - מספר השאלות בתרגיל', 'GENERAL_TAB');
        }

        if (practice.questions.length < 5) {
            return $scope.validationError('יש להגדיר לפחות 5 שאלות לתרגיל', 'QUESTIONS_TAB');
        }

        // question validation
        for (var t=0; t<practice.questions.length; t++) {
            var question = practice.questions[t];
            if (question.answers.length == 0) {
                return $scope.validationError("יש להזין לפחות תשובה אחת לשאלה '" + question.questionStr + "'", 'QUESTIONS_TAB');
            }

            if (question.questionStr.length > 100) {
                return $scope.validationError("הגודל המקסימאלי בשאלה אחת הינו 100 תווים - '" + question.questionStr + "'", 'QUESTIONS_TAB');
            }
        }

        $scope.errorMsg = '';
        return true;
    };

    $scope.validationError = function(msg, activeTab) {
        $scope.setActiveTab(activeTab);
        $scope.showErrorMsg(msg);
        return false;
    };

    $scope.setActiveTab = function(tabId) {
        $scope.tabs[tabId] = {active: true};
        $scope.validateScreenCalculation(50);
    };

    $scope.showErrorMsg = function(errorMsg) {
        var prevOptions = toastr.options;
        toastr.options = {
            "closeButton": false,
            "debug": true,
            "positionClass": "toast-top-full-width",
            "onclick": null,
            "showDuration": "300",
            "hideDuration": "1000",
            "timeOut": "5000",
            "extendedTimeOut": "1000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut"
        };
        toastr.error(errorMsg);
        toastr.options = prevOptions;
    };

    $scope.cancelPracticeEdit = function() {
        bootbox.confirm("האם לבטל את השינויים בתרגיל?", function(userAnswer) {//todo i18n
            if (userAnswer) {
                $scope.$apply(function() {
                    $scope.practiceContainerClass = '';
                    $scope.practiceViewMode = true;
                    if ($scope.practice.id == -1) { // new practice
                        $window.history.back();
                    } else {
                        $scope.revertChanges();
                        $scope.validateScreenCalculation(600);
                    }
                });

            }
        });
    };

    $scope.revertChanges = function() {
        $scope.practice.name = $scope.practiceBeforeEdit.name;
        $scope.practice.description = $scope.practiceBeforeEdit.description;
        $scope.practice.maxMistakesNum = $scope.practiceBeforeEdit.maxMistakesNum;
        $scope.practice.maxSecondsForQuestion = $scope.practiceBeforeEdit.maxSecondsForQuestion;
        $scope.practice.questionsPerPractice = $scope.practiceBeforeEdit.questionsPerPractice;
        $scope.practice.randomOrder = $scope.practiceBeforeEdit.randomOrder;
        $scope.practice.useTTS = $scope.practiceBeforeEdit.useTTS;

        $scope.practice.practiceSharedChildData = $scope.practiceBeforeEdit.practiceSharedChildData;
        $scope.practice.practiceSharedGroupData = $scope.practiceBeforeEdit.practiceSharedGroupData;

        $scope.currentChunck = 3;
        $scope.practice.questions.clear();
        var chunckOfQuestions = $scope.allQuestions.slice(0, $scope.currentChunck * $scope.scrollChanck);
        for (var index=0; index<chunckOfQuestions.length; index++) {   // implementing deep copy of the questions in chunckQuestions
            var currentQuestion = chunckOfQuestions[index];
            $scope.practice.questions.push(angular.copy(currentQuestion));
        }
        $scope.practice.deletedQuestionsIds = [];

        $timeout(function() {
            msgBus.emitMsg('practiceEditReverted', $scope.practice.id);     // notify the questions to refresh the data, because i'm using property and not functions
        }, 50);
    };

    msgBus.onMsg('deleteQuestionMsg', function(event, questionToRemove) {
        questionToRemove.entityState = 'DELETED';
        $scope.practice.deletedQuestionsIds.push(questionToRemove.id);
        $scope.removeFromQuestionsAry(questionToRemove);
    }, $scope);

    $scope.removeFromQuestionsAry = function (questionToRemove) {
        var questionsAry = $scope.practice.questions;
        for (var i = 0; i < questionsAry.length; i++) {
            var question = questionsAry[i];
            if (question.$$hashKey == questionToRemove.$$hashKey) {
                questionsAry.splice(i, 1);
                return;
            }
        }
    };

    function onResize() {
        var heading = $('#practice-page-heading');
        var sideBar = $('#sidebar-rtl');
        var singlePracticeContainer = $('#single-practice-container');
        var tabTitlePane = singlePracticeContainer.find('.nav-tabs');
        var controlPanel = $('#single-practice-controls-panel');

        var windowWidth = $(window).width();
        if (windowWidth >= 768) {
            var rightVal = windowWidth - sideBar.offset().left;
            var leftVal = singlePracticeContainer.offset().left;
            heading.css('right', rightVal);
            heading.css('left', leftVal);

            tabTitlePane.css('right', rightVal);
            tabTitlePane.css('left', leftVal);

            controlPanel.css('right', rightVal);
            controlPanel.css('left', leftVal);
        } else {
            var paddingVal = 0;
            heading.css('right', paddingVal);
            heading.css('left', paddingVal);

            tabTitlePane.css('right', paddingVal);
            tabTitlePane.css('left', paddingVal);

            controlPanel.css('right', 0);
            controlPanel.css('left', 0)
        }
        heading.css('top', 51);
        $('#practice-tabset').css('padding-top', heading.outerHeight() -10);

        $('.practice-page-loader').fadeOut();//todo put this line only in single place
    }
}