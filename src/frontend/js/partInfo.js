let categoryName;
let carArr = [];
let userName;
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
        showLoggedUserInfo(result);
        currentRole = result.role;
    },
    error: function(xhr, status, code) {
        window.location.href = "login.html";
    }
});

function onPageLoaded() {
    showElement("#returnToDashboardContainer");

    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/parts/' + id,
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            hideElement("#loadingContainer");
            hideElement("#errorMessageContainer");
            showElement("#partForm");
            configurePartImage(result);
            getAvailableCategories(result);
        },
        error: function(xhr, status, code) {
            hideElement("#loadingContainer");
            showElement("#errorMessageContainer");
            onPartInfoPageLoadError(xhr, status, code);
        }
    });
}

function configurePartImage(part) {
    let partImage = "https://i.fbcd.co/products/original/7429b506e4f5cfc0d97dd81e0e6fdb6dbfd0dd8a902fedb8ac86738be68dfb66.jpg";
    if (part.pictureUrl !== "") {
        partImage = part.pictureUrl;
    }

    let partImageCode = '<img src="' + partImage + '" class="img-fluid image-fixed-height">';
    $("#partImage").append(partImageCode);
}

function getAvailableCategories(part) {
    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/parts/categories',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            parseCategories(result, part.categoryId);
            getCarsAvailable(part);
        },
        error: function(xhr, status, code) {
            onPartInfoPageLoadError(xhr, status, code);
        }
    });
}

function getCarsAvailable(part) {
    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/parts/cars',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            parseCars(result, part.carIds);
            getAvailableUsers(part);
        },
        error: function(xhr, status, code) {
            onPartInfoPageLoadError(xhr, status, code);
        }
    });
}

function getAvailableUsers(part) {
    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/users',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            parseUsers(result, part.userId)
            parsePart(part);
        },
        error: function(xhr, status, code) {
            onPartInfoPageLoadError(xhr, status, code);
        }
    });
}

function onPartInfoPageLoadError(xhr, status, code) {
    hideElement("#loadingContainer");
    showElement("#returnToPartsListingContainer");
    showPartInfoError(xhr, status, code);
}

function showPartInfoError(xhr, status, code) {
    const errorMessageContent = $("#errorMessageContent");
    errorMessageContent.text("");
    showElement("#errorMessageContainer");
    hideElement("#successMessageContainer");

    if (xhr.status === 404 || xhr.status === 400) {
        const errorMessage = JSON.parse(xhr.responseText).errorMessage;
        const errorFields = ["name", "quantity", "price", "categoryId", "carIds", "oem"];

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

function parseCategories(arr, currentPartCategoryId) {
    categories = arr;
    for (const category of categories) {
        if (currentPartCategoryId === category.id) {
            categoryName = category.name;
        }
    }
}

function parseCars(arr, carIdsArr) {
    cars = arr;
    for (const car of cars) {
        if (carIdsArr.includes(car.id)) {
            carArr.push(car.carBrand.brand + ' ' + car.model);
        }
    }
}

function parseUsers(arr, currentUserId) {
    users = arr;
    for (const user of users) {
        if (currentUserId === user.id) {
            userName = user.username;
        }
    }
}

function parsePart(part) {
    $("#partNameField").val(part.name);
    $("#oemNumberField").val(part.oem);
    $("#quantityField").val(part.quantity);
    $("#priceField").val(part.price);
    $("#discountField").val(part.discount + "%");
    $("#categoryField").val(categoryName);

    let carsNameString = carArr.join(', ');
    $("#supportedCarsField").val(carsNameString);
    $("#byUserField").val(userName);

    configurePartActions(part, categoryName, carsNameString);
}

function configurePartActions(part, category, carsString) {
    let actions = "";
    
    if (currentRole === "Manager") {
        actions += '<a href="manager/edit.html?id=' + part.id + '"><button type="button" class="btn btn-warning mr-2" data-mdb-ripple-color="light">Edit</button></a> \
<button type="button" class="btn btn-danger mr-2" data-mdb-ripple-color="light" data-mdb-toggle="modal" data-mdb-target="#deletePartModal" onclick="changePartDeleteModal(' + part.id + ', \'' + part.name + '\', \'' + category + '\', \'' + carsString + '\')">Delete</button>';
    }

    if (currentRole === "Employee") {
        actions += '<a href="sell.html?id='+ part.id + '"> <button type="button" class="btn btn-success mr-2">Sell</button></a> ';
    }
    
    if (part.discount > 5 && part.discount < 95 && currentRole === "Employee") {
        actions += '<button type="button" class="btn text-white" style="background-color: #3b5998;" data-mdb-toggle="modal" data-mdb-target="#shareDiscountModal" onclick="changePartShareModal(\'' + part.name + '\', \'' + part.discount + '\')"><i class="fab fa-facebook-f"></i> Share Discount</button>';
    }

    $("#partActions").append(actions);
}


function changePartDeleteModal(id, name, categoryName, carsString) {
    $("#deletePartModalBody").text("Are you sure you want to delete part '" +  name + "' (ID " + id + "), category - '" + categoryName + "' , cars - '" + carsString + "' ?");
    $("#deletePartModalYesButton").attr("onclick", "deletePart(" + id + ")");
}

function deletePart(id) {
    $.ajax({
        type: 'DELETE',
        url: APICONFIG.host + '/parts/' + id ,
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            window.location.href = "dashboard.html";
        },
        error: function (xhr, status, code) {
            $("#deletePartModalNoButton").click();
            showElement("#errorMessageContainer");
            if (xhr.status == 404 || xhr.status == 400 || xhr.status == 401) {
                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                return;
            }

            $("#errorMessageContent").append("Cannot connect to server");
        }
    });
}

function changePartShareModal(name, partDiscount) {
    $("#shareDiscountModalBody").text("Are you sure you want to share part '" +  name + "' in Facebook with discount value '" + partDiscount + "'?");
    $("#shareDiscountModalYesButton").attr("onclick", 'sharePart(\"' + id + '\", \"' + partDiscount + '\")');
}

function sharePart(id, discount) {
    const dataToBeSent = {
        partDiscount: discount
    };

    $.ajax({
        type: 'POST',
        url: APICONFIG.host + '/parts/' + id + '/share',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        data: JSON.stringify(dataToBeSent),
        contentType: "application/json",
        success: function (result) {
            $("#successMessageContent").text("Part discount was successfully shared in Facebook")
            showElement("#successMessageContainer");
            $("#shareDiscountModalNoButton").click();
        },
        error: function (xhr, status, code) {
            $("#shareDiscountModalNoButton").click();
            showElement("#errorMessageContainer");
            if (xhr.status == 404 || xhr.status == 400 || xhr.status == 401) {
                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                return;
            }
            $("#errorMessageContent").append("Cannot connect to server");
        }
    });
}