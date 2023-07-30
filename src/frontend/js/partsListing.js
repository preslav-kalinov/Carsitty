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
        url: APICONFIG.host + '/parts',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            hideElement("#loadingContainer");
            hideElement("#errorMessageContainer");
            showElement("#returnToManagerMenuContainer");
            showElement("#addPartContainer");
            showElement("#partsListingContainer");
            showPartsListing(result);
        },
        error: function(xhr, status, code) {
            hideElement("#loadingContainer");
            showElement("#errorMessageContainer");
            showElement("#returnToManagerMenuContainer");
            hideElement('#addPartContainer');
            hideElement('#partsListingContainer');
            
            if(xhr.status == 404) {
                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                showElement("#addPartContainer");
                return;
            }

            if(xhr.status == 401) {
                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                return;
            }

            $("#errorMessageContent").append("Cannot connect to server");
        }
    });
}

function partSearchInputChanged() {
    const userInput = $("#partSearch").val().trim();
    const userInputRegex = new RegExp("\\b" + quotemeta(userInput), "i");
    $('#partsListingTableContent').find('tr').each(function() {
        const nameColumnText = $(this).find('td#partName').text();
        const carColumnText = $(this).find('td#carBrand').text();
        const hasNameMatch = userInputRegex.test(nameColumnText);
        const hasCarMatch = userInputRegex.test(carColumnText);

        if (!(hasNameMatch || hasCarMatch) && !$(this).hasClass("visually-hidden")) {
            $(this).addClass("visually-hidden");
        } else if ((hasNameMatch || hasCarMatch) && $(this).hasClass("visually-hidden")) {
            $(this).removeClass("visually-hidden");
        }
    });
}

function showPartsListing(result) {
    for(const part of result) {
        let tableRow = "<tr>";
        tableRow += "<td>" + part.id + "</td>";
        tableRow += '<td id="partName">' + part.name + "</td>";
        tableRow += "<td>" + part.quantity + "</td>";
        tableRow += "<td>" + part.price + "</td>";
        tableRow += "<td>" + part.category.name + "</td>";
        tableRow += '<td id="carBrand">' + part.car.carBrand.brand + ' ' + part.car.model + "</td>";
        tableRow += '<td><a href="edit.html?id=' + part.id + '"><button type="button" class="btn btn-outline-warning btn-rounded" data-mdb-ripple-color="light">Edit</button></a> <button type="button" class="btn btn-outline-danger btn-rounded" data-mdb-ripple-color="light" data-mdb-toggle="modal" data-mdb-target="#deletePartModal" onclick="changePartDeleteModal(' + part.id + ', \'' + part.name + '\', \'' + part.category.name + '\', \'' + part.car.carBrand.brand + '\', \'' + part.car.model + '\')">Delete</button> <a href="sell.html?id='+ part.id + '"> <button type="button" class="btn btn-outline-success btn-rounded" data-mdb-ripple-color="light">Sell</button></a></td>';
        tableRow += "</tr>";
        $("#partsListingTableContent").append(tableRow);
    }
}

function changePartDeleteModal(id, name, categoryName, carBrand, carModel){
    $("#deletePartModalBody").text("Are you sure you want to delete part '" +  name + "' (ID " + id + "), category - '" + categoryName + "' , car - '" + carBrand + " " + carModel + "' ?");
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
            location.reload();
        },
        error: function (xhr, status, code) {
            $("#deletePartModalNoButton").click();
            showElement("#errorMessageContainer");
            hideElement('#addPartContainer');
            hideElement('#partsListingContainer');
            if(xhr.status == 404 || xhr.status == 400 || xhr.status == 401) {
                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                return;
            }
            $("#errorMessageContent").append("Cannot connect to server");
        }
    });
}