$( document ).ready(function() {
    $('.nav.navbar-nav.navbar-right li').click(function () {
        closeMenu();
    });

    $("#contact-us").click(function () {
        closeMenu();
    });

    setTimeout(function(){
        $('.nav.navbar-nav li').removeClass('active');
        $('#navbar-home').addClass('active-alt');
    }, 1000);

    $('.nav.navbar-nav li').click(function(event) {
        $('.nav.navbar-nav li').removeClass('active-alt');
        $(event.target).closest("li").toggleClass('active-alt');
    });

    $('#contact-us').click(function(event) {
        $('.nav.navbar-nav li').removeClass('active-alt');
    });
});

function closeMenu() {
    if ($(document).width() <= 767) {
        $('.navbar-toggle').click();
    }
}

function sendMessage() {
    var form = $('#contact-us-form');
    var	url = form.attr('action');
    var posting = $.post(url,  form.serialize());
    posting.done(function(response) {
        $("form#contact-us-form :input").each(function(){
            $(this).val('');
        });
        alert('ההודעה נשלחה בהצלחה, מודים על פנייתך (-:');
    });
}
