function parentChildsCtrl($scope, yesNoModal, parentDataService) {

    parentDataService.getAllChilds()
        .then(function (childs) {
            $scope.childs = childs;
            if (childs.length == 0 && getCookie(PARENT_SAW_WELCOME_MSG_COOKIE) != 'true') {
                setCookie(PARENT_SAW_WELCOME_MSG_COOKIE, true);
                if (!isMobileOrTablet()) {
                    inline_manual_player.activateTopic(2028);
                }
                //yesNoModal.open("ברוכים הבאים", 'בשלב הראשון לאחר הרישום יש להגדיר את המשתמשים עבור הילדים שלכם, תוכלו להשתמש באחד משני הכפתורים הירוקים בחלק העליון של הדף עפ"י מין הילד. מין הילד משתמש לקביעת הצבעים בממשק הילדים', function(){}, 'הבנתי', '');
            }
        });

    $scope.enableAddBtn = true;

    $scope.addMaleChild = function() {
        $scope.setEnableAddBtn();
        $scope.childs.unshift({newUser: true, gender: 'MALE', profileImageUrl: 'avatars:svg-2'});
    };

    $scope.addFemaleChild = function() {
        $scope.setEnableAddBtn();
        $scope.childs.unshift({newUser: true, gender: 'FEMALE', profileImageUrl: 'avatars:svg-13'});
    };

    $scope.setEnableAddBtn = function(enable) {
        $scope.enableAddBtn = enable;
    };

    setActiveMenu('#child-btn');
}