let categories;
let cars;
let categoryChosen;
let carChosen;
let currentRole;

const urlParams = new URLSearchParams(window.location.search);
const id = urlParams.get('id');

$.ajax({
    type: 'GET',
    url: APICONFIG.host + '/me',
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true,
    success: function(result) {
        currentRole = result.role;
        showLoggedUserInfo(result);
    },
    error: function(xhr, status, code) {
        window.location.href = "../login.html";
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
            configureReturnToPartInfoBtn();
            parseCategories(result);
            getCarsAvailable();
        },
        error: function(xhr, status, code) {
            onEditPartPageLoadError(xhr, status, code);
        }
    });
}

function configureReturnToPartInfoBtn() {
    let button = '<a href="../part.html?id=' + id + '"><button type="button" class="btn btn-primary btn-lg btn-block">Return to Part Info</button></a> \
    <hr class="my-4" />';
    $("#returnToPartInfoContainer").append(button);
}

function onEditPartPageLoadError(xhr, status, code) {
    hideElement("#loadingContainer");
    showElement("#returnToPartInfoContainer");
    showEditPartError(xhr, status, code);
}

function showEditPartError(xhr, status, code) {
    const errorMessageContent = $("#errorMessageContent");
    errorMessageContent.text("");
    showElement("#errorMessageContainer");
    hideElement("#successMessageContainer");

    if (xhr.status === 404 || xhr.status === 400) {
        const errorMessage = JSON.parse(xhr.responseText).errorMessage;
        const errorFields = ["name", "quantity", "price", "discount", "categoryId", "carIds", "oem"];

        errorMessageContent.append(errorMessage.problem);
        errorFields.forEach((field) => {
            if (errorMessage[field] !== undefined) {
                errorMessageContent.append("<ul><li>" + errorMessage[field] + "</li></ul>");
            }
        });

        return;
    }

    if (xhr.status == 401 || xhr.status == 403) {
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
            getOnePart();
        },
        error: function(xhr, status, code) {
            onEditPartPageLoadError(xhr, status, code);
        }
    });
}

function getOnePart() {
    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/parts/' + id,
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            parsePart(result);
            hideElement("#loadingContainer");
            showElement("#returnToPartInfoContainer");
            showElement("#editPartForm");
        },
        error: function(xhr, status, code) {
            onEditPartPageLoadError(xhr, status, code);
        }
    });
}

function parsePart(part) {
    $("#partId").val(part.id);
    $("#partPicture").val(part.pictureUrl);
    $("#partName").val(part.name);
    $("#oemNumber").val(part.oem);
    $("#partQuantity").val(part.quantity);
    $("#partPrice").val(part.price);
    $("#partDiscount").val(part.discount);

    $('#categoryItems').find('a').each(function() {
        if ($(this).attr('categoryId') == part.categoryId) {
            if (!$(this).hasClass("active")) {
                $(this).addClass("active");
            }
            categoryChosen = part.categoryId;
        }
    });

    carChosen = part.carIds;
    $('#carItems').find('a').each(function() {
        const carItemId = parseInt($(this).attr('carId'));
        if (carChosen.includes(carItemId)) {
            if (!$(this).hasClass("active")) {
                $(this).addClass("active");
            }
        } else {
            if ($(this).hasClass("active")) {
                $(this).removeClass("active");
            }
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
        $("#carItems").append('<a carId="' + car.id + '" class="dropdown-item" onclick="setCar(' + car.id + '); event.stopPropagation();">' + car.carBrand.brand + ' ' + car.model + '</a>');
    }
}

function setCategory(id) {
    categoryChosen = id;
    $('#categoryItems').find('a').each(function() {
        if ($(this).attr('categoryId') == id && !$(this).hasClass("active")) {
            $(this).addClass("active");
        } else if ($(this).attr('categoryId') != id && $(this).hasClass("active")) {
            $(this).removeClass("active");
        }
    });
}

function setCar(id) {
    const carId = parseInt(id);
    if (carChosen.includes(carId)) {
        carChosen = carChosen.filter(car => car !== carId);
    } else {
        carChosen.push(carId);
    }
    
    $('#carItems').find('a').each(function() {
        const carItemId = parseInt($(this).attr('carId'));
        if (carChosen.includes(carItemId)) {
            if (!$(this).hasClass("active")) {
                $(this).addClass("active");
            }
        } else {
            if ($(this).hasClass("active")) {
                $(this).removeClass("active");
            }
        }
    });
}

function carSearchInputChanged() {
    const userInputRegex = new RegExp(quotemeta($("#carSearch").val()), "i");
    $('#carItems').find('a').each(function() {
        if (!userInputRegex.test($(this).text()) && !$(this).hasClass("visually-hidden")) {
            $(this).addClass("visually-hidden");
        } else if (userInputRegex.test($(this).text()) && $(this).hasClass("visually-hidden")) {
            $(this).removeClass("visually-hidden");
        }
    });
}

function categorySearchInputChanged() {
    const userInputRegex = new RegExp(quotemeta($("#categorySearch").val()), "i");
    $('#categoryItems').find('a').each(function() {
        if (!userInputRegex.test($(this).text()) && !$(this).hasClass("visually-hidden")) {
            $(this).addClass("visually-hidden");
        } else if (userInputRegex.test($(this).text()) && $(this).hasClass("visually-hidden")) {
            $(this).removeClass("visually-hidden");
        }
    });
}

function submitPart() {
    showElement("#formSubmitLoadingContainer");
    hideElement("#formSubmitButton");
    const dataToBeSent = {
        pictureUrl: $("#partPicture").val(),
        name: $("#partName").val(),
        oem: $("#oemNumber").val(),
        quantity: $("#partQuantity").val(),
        price: $("#partPrice").val(),
        discount: $("#partDiscount").val(),
        categoryId: categoryChosen,
        carIds: carChosen
    };
    $.ajax({
        type: 'PUT',
        url: APICONFIG.host + '/parts/' + id,
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        data: JSON.stringify(dataToBeSent),
        contentType: "application/json",
        success: function (result) {
            hideElement("#formSubmitLoadingContainer");
            showElement("#formSubmitButton");
            partEditedSuccessfully();
        },
        error: function (xhr, status, error) {
            hideElement("#formSubmitLoadingContainer");
            showElement("#formSubmitButton");
            showEditPartError(xhr, status, error);
        }
    });
}

function partEditedSuccessfully() {
    hideElement("#errorMessageContainer");
    showElement("#successMessageContainer");
}