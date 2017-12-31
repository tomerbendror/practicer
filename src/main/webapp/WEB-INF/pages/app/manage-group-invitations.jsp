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
    <link rel="stylesheet" href="styles/typeahead.min.css">
    <!-- END DEPENDENCIES -->

    <!-- PAGES -->
    <link rel="stylesheet" href="styles/pages-signin.min.css">
    <!-- END PAGES -->

    <link rel="stylesheet" href="../../css/invitationManagement.css">

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
        <div class="tab-pane active" id="signin">
            <h2 class="signin-brand animated-hue"><a href="/"><i class="fa fa-paper-plane"></i> Practicer</a></h2>
            <div class="invitation-management">
                <form method="post" action="/parent/acceptInvitations">
                    <c:choose>
                        <c:when test="${unusedInvitations.size() == 1}">
                            <div><span class="single-invitation">ממתינה לך הזמנה אחת לקבוצה <strong>${unusedInvitations.get(0).parentsGroup.name}</strong> שנשלחה ע''י <strong>${unusedInvitations.get(0).inviter.displayName}</strong></span></div>
                            <input type="hidden" name="invitationsIds" checked="checked" value="${unusedInvitations.get(0).id}">
                            <br><div><input class="btn btn-primary pull-left join-group-btn" type="submit" value="להצטרפות"/></div>
                        </c:when>
                        <c:otherwise>
                            <div style="padding-bottom: 10px;">
                                <span class="multi-invitation-title">ממתינות לך ${unusedInvitations.size()} הזמנות לקבוצות באתר</span>
                            </div>
                            <table class="pending-invitations-table" width="100%">
                                <tr>
                                    <th>קבוצה</th>
                                    <th>נשלחה ע''י</th>
                                    <th class="join-col">הצטרפות</th>
                                </tr>
                                <c:forEach var="invitation" items="${unusedInvitations}">
                                    <tr>
                                        <td><c:out value="${invitation.parentsGroup.name}"/></td>
                                        <td><c:out value="${invitation.inviter.displayName}"/></td>
                                        <td class="join-col"><input class="js-switch" type="checkbox" name="invitationsIds" checked="checked" value="${invitation.id}"></td>
                                    </tr>
                                </c:forEach>
                            </table>
                            <div style="margin-top: 30px"></div>
                            <input class="btn btn-primary pull-left join-group-btn" style="" type="submit" value="הצטרפות"/>
                        </c:otherwise>
                    </c:choose>
                </form>
            </div>
            <hr>
            <p><a href="${skipUrl}">דלג לאתר</a></p>
        </div><!-- /.tab-pane -->

    </div><!-- /.tab-content -->
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
<script src="scripts/jquery.validate.min.js"></script>
<script src="scripts/additional-methods.min.js"></script>
<script src="scripts/handlebars.min.js"></script>
<script src="scripts/typeahead.bundle.min.js"></script>
<!-- END DEPENDENCIES -->


<!-- Dummy script -->
<script type="text/javascript" src="scripts/demo/pages-signin-demo.js"></script>
<script type="text/javascript" src="../../js/practice-validation-msg.js"></script>


</body>
</html>