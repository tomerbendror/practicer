jQuery.extend(jQuery.validator.messages, {
    required: "שדה זה הינו חובה",
    remote: "יש לתקן את הערך בשדה",
    email: 'יש למלא כתובת דוא"ל חוקית',
    url: "Please enter a valid URL.",
    date: "Please enter a valid date.",
    dateISO: "Please enter a valid date ( ISO ).",
    number: "Please enter a valid number.",
    digits: "Please enter only digits.",
    creditcard: "Please enter a valid credit card number.",
    equalTo: "יש למלא את אותו הערך שוב",
    maxlength: jQuery.validator.format("יש למלא לא יותר מ-{0} תווים"),
    minlength: jQuery.validator.format("יש למלא לפחות {0} תווים"),
    rangelength: jQuery.validator.format("Please enter a value between {0} and {1} characters long."),
    range: jQuery.validator.format("Please enter a value between {0} and {1}."),
    max: jQuery.validator.format("Please enter a value less than or equal to {0}."),
    min: jQuery.validator.format("Please enter a value greater than or equal to {0}.")
});