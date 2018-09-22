<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;  charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>${title}</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <base href="../../wrapkit/">


    <!-- fav and touch icons -->
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="ico/apple-touch-icon-114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="ico/apple-touch-icon-72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="ico/apple-touch-icon-57-precomposed.png">
    <link rel="shortcut icon" href="ico/favicon.png">
    <link rel="shortcut icon" href="ico/favicon.ico">


    <!-- VENDOR -->
    <link rel="stylesheet" href="styles/jqueryui.min.css">
    <link rel="stylesheet" href="styles/bootstrap.min.css">
    <!-- END VENDOR -->

    <!-- WRAPKIT -->
    <link rel="stylesheet" href="styles/wrapkit.min.css">
    <link rel="stylesheet" href="styles/wrapkit-skins-all.min.css">
    <!-- END WRAPKIT -->

    <!-- !IMPORTANT DEPENDENCIES -->
    <link rel="stylesheet" href="styles/font-awesome.min.css">
    <link rel="stylesheet" href="styles/switchery.min.css">
    <link rel="stylesheet" href="styles/toastr.min.css">
    <link rel="stylesheet" href="styles/prettify.min.css">
    <!-- END !IMPORTANT DEPENDENCIES -->

    <!-- DEPENDENCIES -->
    <link rel="stylesheet" href="styles/typeahead.min.css">
    <!-- END DEPENDENCIES -->

    <!-- PAGES -->
    <link rel="stylesheet" href="styles/pages-signin.min.css">
    <!-- END PAGES -->

    <link rel="stylesheet" href="../../css/loginSignup.css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
</head>

<body style="direction: rtl">

<main class="signin-wrapper">
    <div class="tab-content">
        <div class="tab-pane ${signInActive}" id="signin">
            <h2 class="signin-brand animated-hue"><a href="/"><i class="fa fa-paper-plane"></i> Practicer</a></h2>
            <form:form id="signinForm" method="post" action='/user/login' onsubmit="return false" modelAttribute="user" role="form">
                <p class="lead">כניסה לחשבון</p>
                <div id="login-error-panel" class="alert alert-danger" style="display: ${errorPanelDisplay}">
                    <p><strong id="login-error-msg">${errorMsg}</strong></p>
                </div>
                <div class="form-group">
                    <div class="input-group input-group-in">
                        <span class="input-group-addon"><i class="fa fa-user"></i></span>
                        <input name="userName" id="username" class="form-control" placeholder='כתובת דוא"ל / שם משתמש'>
                    </div>
                </div><!-- /.form-group -->
                <div class="form-group">
                    <div class="input-group input-group-in">
                        <span class="input-group-addon"><i class="fa fa-lock"></i></span>
                        <form:input type="password" class="form-control" path="password" id="passwordInput" placeholder="סיסמה"/>
                    </div>
                </div><!-- /.form-group -->
                <div class="form-group clearfix">
                    <div class="animated-hue pull-left">
                        <button id="btnSignin" onclick="loginUser();" class="btn btn-primary">התחבר <i class="fa fa-chevron-circle-left"></i></button>
                    </div>
                        <%--<div class="nice-checkbox nice-checkbox-inline">--%>
                        <%--<label for="keepSignin">השאר אותי מחובר</label>--%>
                        <%--<input type="checkbox" name="keepSignin" id="keepSignin" checked="checked">--%>
                        <%--</div>--%>
                </div><!-- /.form-group -->

                <hr>


                <p><a href="/user/resetPassword">שכחתי את הסיסמה</a></p>
                <%--<p class="lead" style="margin-bottom: 10px;">כניסה בעזרת חשבון אחר</p>--%>
                <div class="signin-alt">
                        <%--<a href="/social/login?id=facebook" class="btn btn-sm btn-belpet"><i class="fa fa-facebook"></i></a>--%>
                        <%--<a href="#" class="btn btn-sm btn-info"><i class="fa fa-twitter"></i></a>--%>
                            <a href="/social/login?id=google" class="btn btn-sm btn-danger btn-block google-login-btn">
                                <span class="sign-in-text">כניסה בעזרת חשבון Google</span>
                            </a>
                        <%--<a href="#" class="btn btn-sm btn-silc"><i class="fa fa-github"></i></a>--%>
                </div>

                <hr>




                <p class="no-account-p">אין לך חשבון? <a href="#signup" data-toggle="tab">יצירת חשבון <strong style="text-decoration: underline;">הורה</strong> חדש </a></p>
                <p style="text-decoration: underline;"><strong>ליצירת חשבון ילד יש להרשם תחילה כהורה ולאחר מכן להגדיר את הילדים</strong></p>
            </form:form>
        </div><!-- /.tab-pane -->

        <div class="tab-pane ${signUpActive}" id="signup">
            <h2 class="signin-brand animated-hue"><a href="/"><i class="fa fa-paper-plane"></i> Practicer</a></h2>
            <form:form id="signupForm" method="post" modelAttribute="user" role="form" action="/user/signup" onsubmit="return false" >
                <p class="lead"> יצירת חשבון <strong style="text-decoration: underline;">הורה</strong> חדש</p>
                <span>ליצירת חשבון ילד יש להרשם תחילה כהורה ולאחר מכן להגדיר את הילדים</span><br><br>
                <div id="signup-error-panel" class="alert alert-danger" style="display: none">
                    <p><strong id="signup-error-msg">${errorMsg}</strong></p>
                </div>
                <div class="signin-alt">
                        <%--<a href="/social/login?id=facebook" class="btn btn-sm btn-belpet"><i class="fa fa-facebook"></i></a>--%>
                        <%--<a href="#" class="btn btn-sm btn-info"><i class="fa fa-twitter"></i></a>--%>
                    <a href="/social/login?id=google" class="btn btn-sm btn-danger btn-block google-login-btn google-login-btn">
                        <span class="sign-in-text">כניסה בעזרת חשבון Google</span>
                    </a>
                        <%--<a href="#" class="btn btn-sm btn-silc"><i class="fa fa-github"></i></a>--%>
                </div>
                <p class="text-muted"><strong>הכנס פרטים אישיים</strong></p>
                <div class="form-group has-feedback">
                    <div class="input-group input-group-in">
                        <span class="input-group-addon"><i class="fa fa-font"></i></span>
                        <form:input type="text" class="form-control" path="firstName" id="firstName" placeholder="שם פרטי"/>
                        <span class="form-control-feedback"></span>
                    </div>
                </div><!-- /.form-group -->
                <div class="form-group has-feedback">
                    <div class="input-group input-group-in">
                        <span class="input-group-addon"><i class="fa fa-font"></i></span>
                        <form:input type="text" class="form-control"  path="lastName" id="lastName" placeholder="שם משפחה"/>
                        <span class="form-control-feedback"></span>
                    </div>
                </div><!-- /.form-group -->

                <!--
                <div class="form-group has-feedback">
                <div class="input-group input-group-in">
                <span class="input-group-addon"><i class="fa fa-road"></i></span>
                <input name="address" id="address" class="form-control" placeholder="Address">
                <span class="form-control-feedback"></span>
                </div>
                </div><!-- /.form-group -->


                <!-- <div class="form-group has-feedback">
                <div class="input-group input-group-in">
                <span class="input-group-addon"><i class="fa fa-map-marker"></i></span>
                <input name="city" id="city" class="form-control" placeholder="City">
                <span class="form-control-feedback"></span>
                </div>
                </div><!-- /.form-group -->
                <!--<div class="form-group has-feedback">
                <div class="input-group input-group-in">
                <span class="input-group-addon" title="unable to find any Country that match the current query!"><i class="fa fa-flag"></i></span>
                <input name="country" id="country" class="form-control" placeholder="Countries">
                <span class="form-control-feedback"></span>
                </div><!-- /input-group-in -->
                <!--</div><!-- /.form-group -->
                <!--<div class="form-group">
                <label class="control-label" style="margin-right:15px">Gender</label>
                <div class="nice-radio nice-radio-inline">
                <input type="radio" name="gender" id="genderMale" value="male" checked="checked">
                <label for="genderMale">Male</label>
                </div><!-- /.radio -->
                <!--<div class="nice-radio nice-radio-inline">
                <input type="radio" name="gender" id="genderFemale" value="female">
                <label for="genderFemale">Female</label>
                </div><!-- /.radio -->
                <!--</div><!-- /.form-group -->

                <!-- <hr> -->

                <p class="text-muted"><strong>הכנס פרטי התחברות</strong></p>
                <div class="form-group has-feedback">
                    <div class="input-group input-group-in">
                        <span class="input-group-addon"><i class="fa fa-envelope"></i></span>
                        <form:input type="text" class="form-control" path="email" id="email" placeholder='כתובת דוא"ל'/>
                        <span class="form-control-feedback"></span>
                    </div>
                </div><!-- /.form-group -->
                <div class="form-group has-feedback">
                    <div class="input-group input-group-in">
                        <span class="input-group-addon"><i class="fa fa-key"></i></span>
                        <form:input type="password" class="form-control" path="password" id="password" placeholder="סיסמה"/>
                        <span class="form-control-feedback"></span>
                    </div>
                </div><!-- /.form-group -->
                <div class="form-group has-feedback">
                    <div class="input-group input-group-in">
                        <span class="input-group-addon"><i class="fa fa-check"></i></span>
                        <input type="password" name="cpassword" id="cpassword" class="form-control" placeholder="הזן סיסמה שנית">
                        <span class="form-control-feedback"></span>
                    </div>
                </div><!-- /.form-group -->
                <div class="form-group animated-hue clearfix">
                    <div class="pull-left">
                        <button id="btnSignup" onclick="signupUser();" class="btn btn-primary">יצירת חשבון <i class="fa fa-chevron-circle-left"></i></button>
                    </div>
                    <div class="pull-right">
                        <a href="#signin" class="btn btn-default" data-toggle="tab"><i class="fa fa-chevron-circle-right fa-fw"></i> יש לי חשבון</a>
                    </div>
                </div><!-- /.form-group -->
            </form:form><!-- /#signupForm -->

            <%--<hr>--%>

            <%--<p>By creating an account you agree to the <a href="#">Terms of Use</a> and <a href="#">Privacy Policy</a></p>--%>
        </div><!-- /.tab-pane -->
    </div><!-- /.tab-content -->
</main><!--/#wrapper-->
<%--<div class="signin-cr">&copy; 2014 Wrapkit. with Love from Stilearning team.</div>--%>


<!-- Modal Recover -->
<div class="modal fade" id="recoverAccount" tabindex="-1" role="dialog" aria-labelledby="recoverAccountLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="recoverForm" action="#">
                <div class="modal-header">
                    <h4 class="modal-title" id="recoverAccountLabel">Reset Password</h4>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <div class="input-group input-group-in">
                            <span class="input-group-addon"><i class="fa fa-envelope-o text-muted"></i></span>
                            <input type="email" name="recoverEmail" id="recoverEmail" class="form-control" placeholder="Enter your email address">
                        </div>
                    </div><!-- /.form-group -->
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Send reset link</button>
                </div>
            </form><!-- /#recoverForm -->
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /#recoverAccount -->


<!-- VENDOR -->
<script src="scripts/jquery.min.js"></script>
<script src="scripts/bootstrap.min.js"></script>
<script src="scripts/jquery-ui.min.js"></script>
<!-- END VENDOR -->


<!-- !IMPORTANT DEPENDENCIES -->
<script src="scripts/jquery.ui.touch-punch.min.js"></script>
<script src="scripts/jquery.cookie.min.js"></script>
<script src="scripts/screenfull.min.js"></script>
<script src="scripts/jquery.autogrowtextarea.min.js"></script>
<script src="scripts/jquery.nicescroll.min.js"></script>
<script src="scripts/bootbox.min.js"></script>
<script src="scripts/switchery.min.js"></script>
<script src="scripts/toastr.min.js"></script>
<script src="scripts/components-setup.min.js"></script>
<!-- END !IMPORTANT DEPENDENCIES -->

<!-- WRAPKIT -->
<script src="scripts/wrapkit-utils.min.js"></script>
<!-- END WRAPKIT -->

<!-- DEPENDENCIES -->
<script src="scripts/jquery.vegas.min.js"></script>
<script src="scripts/jquery.validate.min.js"></script>
<script src="scripts/additional-methods.min.js"></script>
<script src="scripts/handlebars.min.js"></script>
<script src="scripts/typeahead.bundle.min.js"></script>
<!-- END DEPENDENCIES -->


<!-- Dummy script -->
<script type="text/javascript" src="/js/app/common-app.js"></script>
<script type="text/javascript" src="scripts/demo/pages-signin-demo.js"></script>
<script type="text/javascript" src="../../js/practice-validation-msg.js"></script>


</body>
</html>