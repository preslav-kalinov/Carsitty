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
        showLoggedUserInfo(result)
    },
    error: function(xhr, status, code) {
        window.location.href = "login.html";
    }
});

function onPageLoaded() {
    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/parts/' + id,
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            parsePart(result);
            configureReturnToPartInfoBtn();
            hideElement("#loadingContainer");
            showElement("#returnToPartInfoContainer");
            showElement("#sellPartForm");
        },
        error: function(xhr, status, code) {
            onSellPartPageLoadError(xhr, status, code);
        }
    });
}

function configureReturnToPartInfoBtn() {
    let button = '<a href="part.html?id=' + id + '"><button type="button" class="btn btn-primary btn-lg btn-block">Return to Part Info</button></a> \
    <hr class="my-4" />';
    $("#returnToPartInfoContainer").append(button);
}

function onSellPartPageLoadError(xhr, status, code) {
    hideElement("#loadingContainer");
    showElement("#returnToPartInfoContainer");
    showSellPartError(xhr, status, code);
}

function showSellPartError(xhr, status, code) {
    const errorMessage = JSON.parse(xhr.responseText).errorMessage;
    const errorMessageContent = $("#errorMessageContent");
    errorMessageContent.text("");
    showElement("#errorMessageContainer");
    hideElement("#successMessageContainer");

    if (xhr.status === 404 || xhr.status === 400) {
        const errorFields = ["soldQuantity"];

        errorMessageContent.append(errorMessage.problem);
        errorFields.forEach((field) => {
            if (errorMessage[field] !== undefined) {
                errorMessageContent.append("<ul><li>" + errorMessage[field] + "</li></ul>");
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

function parsePart(part) {
    $("#partId").val(part.id);
    $("#partName").val(part.name);
    $("#partQuantity").val(part.quantity);
    $("#partPrice").val(part.price);
    $("#partSoldQuantity").val(1);
}

function submitPart() {
    showElement("#formSubmitLoadingContainer");
    hideElement("#formSubmitButton");
    const dataToBeSent = {
        soldQuantity: $("#partSoldQuantity").val()
    };
    $.ajax({
        type: 'POST',
        url: APICONFIG.host + '/parts/' + id + "/sale",
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        data: JSON.stringify(dataToBeSent),
        contentType: "application/json",
        success: function (result) {
            hideElement("#formSubmitLoadingContainer");
            showElement("#formSubmitButton");
            partSoldSuccessfully(result);
        },
        error: function (xhr, status, error) {
            hideElement("#formSubmitLoadingContainer");
            showElement("#formSubmitButton");
            showSellPartError(xhr, status, error);
        }
    });
}

function partSoldSuccessfully(part) {
    hideElement("#errorMessageContainer");
    showElement("#successMessageContainer");
    $("#partQuantity").val(part.quantity);
    $("#partSoldQuantity").val(1);
}