<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
    <link rel="stylesheet" href="styles/vegas.min.css">
    <!-- END DEPENDENCIES -->

    <!-- PAGES -->
    <link rel="stylesheet" href="styles/pages-locked.min.css">
    <!-- END PAGES -->

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->

    <link rel="stylesheet" href="../../css/resetPassword.css">
</head>
<body>
<main class="locked-wrapper">
    <section class="locked-container">
        <%--<h2 class="locked-brand">{ <i class="fa fa-rocket"></i> Wrapkit }</h2>--%>
        <h2 class="locked-brand signin-brand animated-hue"><a href="/"><i class="fa fa-paper-plane"></i> Practicer</a></h2>
        <div class="locked-actions">
            <a href="/user/login" class="btn btn-sm btn-danger" rel="tooltip" title="כניסה"><i class="fa fa-sign-out"></i></a>
        </div>
        <div class="locked-avatar">
            <div class="kit-avatar kit-avatar-128">
                <img src="images/dummy/profile.jpg" alt="photo profile">
            </div>
        </div>
        <p class="locked-email"></p>
        <c:choose>
            <c:when test="${userNotFound}">
                <p class="locked-email">משתמש לא קיים</p>
            </c:when>
            <c:otherwise>

                <c:choose>
                    <c:when test="${afterSend}">
                        <p class="locked-email">סיסמה חדשה נשלחה למייל</p>
                    </c:when>
                    <c:otherwise>
                        <form:form id="resetPasswordForm" method="post" action='/user/resetPassword' modelAttribute="user" role="form">
                            <div id="lockedInput" class="form-group" style="margin-bottom: 14px;">
                                <div class="input-group input-group-in">
                                    <form:input type="email" class="form-control" path="email" id="email" cssStyle="direction: ltr;" placeholder='כתובת דוא"ל' />
                                    <div class="input-group-btn">
                                        <button type="submit" class="btn"><i class="fa fa-chevron-right"></i></button>
                                    </div>
                                </div>
                            </div>

                            <div id="lockedLoader" class="form-group hide">
                                <div class="locked-loader">
                                    <span class="fa fa-spin fa-spinner"></span>
                                </div>
                            </div>
                        </form:form>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </section><!-- /.locked-container -->
    <%--<div class="locked-cr">&copy; 2014 Wrapkit. with Love from Stilearning team.</div>--%>
</main><!--/#wrapper-->


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
<!-- END DEPENDENCIES -->


<!-- Dummy script -->
<script type="text/javascript" src="scripts/demo/locked-demo.js"></script>
</body>
</html>
