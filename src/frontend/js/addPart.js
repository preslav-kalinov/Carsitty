let categories;
let cars;
let categoryChosen;
let carChosen;

$.ajax({
    type: 'GET',
    url: APICONFIG.host + '/me',
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true,
    success: function(result) {
        showLoggedUserInfo(result)
    },
    error: function(xhr, status, code) {
        window.location.href = "login.html";
    }
});

function onPageLoaded() {
    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/parts/categories',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            parseCategories(result);
            getCarsAvailable();
        },
        error: function(xhr, status, code) {
            onAddPartPageLoadError(xhr, status, code);
        }
    });
}

function onAddPartPageLoadError(xhr, status, code) {
    hideElement("#loadingContainer");
    showElement("#returnToPartsListingContainer");
    showAddPartError(xhr, status, code);
}

function showAddPartError(xhr, status, code) {
    const errorMessage = JSON.parse(xhr.responseText).errorMessage;
    const errorMessageContent = $("#errorMessageContent");
    errorMessageContent.text("");

    showElement("#errorMessageContainer");
    hideElement("#successMessageContainer");

    if (xhr.status === 404 || xhr.status === 400) {
        const errorFields = ["name", "quantity", "price", "categoryId", "carId"];

        errorMessageContent.append(errorMessage.problem);
        errorFields.forEach((field) => {
            if (errorMessage[field] !== undefined) {
                errorMessageContent.append("<br>" + errorMessage[field]);
            }
        });

        return;
    }

    if(xhr.status == 401 || xhr.status == 403) {
        errorMessageContent.append(errorMessage.problem);
        return;
    }

    $("#errorMessageContent").append("Cannot connect to server");
}

function getCarsAvailable() {
    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/parts/cars',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            parseCars(result);
            hideElement("#loadingContainer");
            showElement("#returnToPartsListingContainer");
            showElement("#addPartForm");
        },
        error: function(xhr, status, code) {
            onAddPartPageLoadError(xhr, status, code);
        }
    });
}

function parseCategories(arr) {
    categories = arr;
    for(const category of categories){
        $("#categoryItems").append('<a categoryId="' + category.id + '" class="dropdown-item" onclick="setCategory(' + category.id + ')">' + category.name + '</a>');
    }
}

function parseCars(arr) {
    cars = arr;
    for(const car of cars){
        $("#carItems").append('<a carId="' + car.id + '" class="dropdown-item" onclick="setCar(' + car.id + ')">' + car.carBrand.brand + ' ' + car.model + '</a>');
    }
}

function setCategory(id) {
    categoryChosen = id;
    $('#categoryItems').find('a').each(function() {
        if($(this).attr('categoryId') == id && !$(this).hasClass("active")) {
            $(this).addClass("active");
        } else if($(this).attr('categoryId') != id && $(this).hasClass("active")) {
            $(this).removeClass("active");
        }
    });
}

function setCar(id) {
    carChosen = id;
    $('#carItems').find('a').each(function() {
        if($(this).attr('carId') == id && !$(this).hasClass("active")) {
            $(this).addClass("active");
        } else if($(this).attr('carId') != id && $(this).hasClass("active")) {
            $(this).removeClass("active");
        }
    });
}

function carSearchInputChanged() {
    const userInputRegex = new RegExp(quotemeta($("#carSearch").val()), "i");
    $('#carItems').find('a').each(function() {
        if(!userInputRegex.test($(this).text()) && !$(this).hasClass("visually-hidden")) {
            $(this).addClass("visually-hidden");
        } else if(userInputRegex.test($(this).text()) && $(this).hasClass("visually-hidden")) {
            $(this).removeClass("visually-hidden");
        }
    });
}

function categorySearchInputChanged() {
    const userInputRegex = new RegExp(quotemeta($("#categorySearch").val()), "i");
    $('#categoryItems').find('a').each(function() {
        if(!userInputRegex.test($(this).text()) && !$(this).hasClass("visually-hidden")) {
            $(this).addClass("visually-hidden");
        } else if(userInputRegex.test($(this).text()) && $(this).hasClass("visually-hidden")) {
            $(this).removeClass("visually-hidden");
        }
    });
}

function submitPart() {
    showElement("#formSubmitLoadingContainer");
    hideElement("#formSubmitButton");
    const dataToBeSent = {
        name: $("#partName").val(),
        quantity: $("#partQuantity").val(),
        price: $("#partPrice").val(),
        categoryId: categoryChosen,
        carId: carChosen
    };
    $.ajax({
        type: 'POST',
        url: APICONFIG.host + '/parts',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        data: JSON.stringify(dataToBeSent),
        contentType: "application/json",
        success: function (result) {
            hideElement("#formSubmitLoadingContainer");
            showElement("#formSubmitButton");
            partAddedSuccessfully();
        },
        error: function (xhr, status, error) {
            hideElement("#formSubmitLoadingContainer");
            showElement("#formSubmitButton");
            hideElement("#errorMessageContainer");
            showAddPartError(xhr, status, error);
        }
    });
}

function partAddedSuccessfully() {
    hideElement("#errorMessageContainer");
    showElement("#successMessageContainer");
    categoryChosen = undefined;
    carChosen = undefined;
    $("#categorySearch").val("");
    $("#carSearch").val("");
    $('#categoryItems').find('a').each(function() {
        if($(this).hasClass("active")) {
            $(this).removeClass("active");
        }
        if($(this).hasClass("visually-hidden")) {
            $(this).removeClass("visually-hidden");
        }
    });
    $('#carItems').find('a').each(function() {
        if($(this).hasClass("active")) {
            $(this).removeClass("active");
        }
        if($(this).hasClass("visually-hidden")) {
            $(this).removeClass("visually-hidden");
        }
    });
    $("#partName").val("");
    $("#partQuantity").val("");
    $("#partPrice").val("");
}