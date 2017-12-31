<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Practicer</title>
    <base href="../../wrapkit/">

    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <!-- fav and touch icons -->
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="ico/apple-touch-icon-114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="ico/apple-touch-icon-72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="ico/apple-touch-icon-57-precomposed.png">
    <link rel="shortcut icon" href="ico/favicon.png">
    <link rel="shortcut icon" href="ico/favicon.ico">
    <!-- Bootstrap core CSS -->
    <link rel="stylesheet" href="styles/bootstrap.min.css">
    <!-- Fonts  -->
    <link rel="stylesheet" href="styles/font-awesome.min.css">
    <link rel="stylesheet" href="/css/error-page/simple-line-icons.css">
    <!-- CSS Animate -->
    <link rel="stylesheet" href="styles/animate.min.css">
    <!-- Custom styles for this theme -->
    <link rel="stylesheet" href="/css/error-page/main.css">
    <!-- Feature detection -->
    <script src="/js/modernizr-2.6.2.min.js"></script><style type="text/css"></style>
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="js/vendor/html5shiv.js"></script>
    <script src="js/vendor/respond.min.js"></script>
    <![endif]-->
</head>
<body style="direction: rtl;" class=" pace-done" cz-shortcut-listen="true"><div class="pace  pace-inactive"><div class="pace-progress" data-progress-text="100%" data-progress="99" style="width: 100%;">
    <div class="pace-progress-inner"></div>
</div>
    <div class="pace-activity"></div></div>
<section class="container">

    <div class="row">
        <div class="col-md-6 col-md-offset-3">
            <div id="error-container" class="block-error animated flipInX">
                <header>
                    <h1 class="error">403</h1>
                    <p class="text-center">${msg}</p>
                    <c:if test="${subtitle != null && subtitle.trim().length() != 0}">
                        <h2>${subtitle} <a style="text-decoration: underline" href="/user/login">כאן</a></h2><br>
                    </c:if>
                </header>


                <%-- <p class="text-center">Houston, we have a problem. We're having trouble loading the page you are looking for.</p>--%>
                <div class="row">
                    <div class="col-md-6">
                        <a class="btn btn-info btn-block btn-3d" href="${homeUrl}">לעמוד הבית</a>
                    </div>
                    <div class="col-md-6">
                        <button class="btn btn-success btn-block btn-3d" onclick="history.back();">לדף הקודם</button>
                    </div>
                </div>

            </div>
        </div>
    </div>
</section>
<!--Global JS-->
<%--<script src="js/vendor/jquery-1.11.1.min.js"></script>--%>
<%--<script src="js/bootstrap/js/bootstrap.min.js"></script>--%>
<%--<script src="js/plugins/pace/pace.min.js"></script>--%>



</body>
</html>
