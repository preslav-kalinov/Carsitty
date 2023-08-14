let currentRole;
let partId;

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
            
            if (currentRole === "Manager") {
                showElement("#returnToManagerMenuContainer");
                showElement("#addPartContainer");
            }
            
            showElement("#partsListingContainer");
            showPartsListing(result);
        },
        error: function(xhr, status, code) {
            hideElement("#loadingContainer");
            showElement("#errorMessageContainer");

            if (currentRole === "Manager") {
                showElement("#returnToManagerMenuContainer");
            }

            hideElement('#addPartContainer');
            hideElement('#partsListingContainer');
            
            if(xhr.status == 404) {
                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                
                if (currentRole === "Manager") {
                    showElement("#addPartContainer");
                }

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
    let userInputRegex;

    try {
        userInputRegex = new RegExp(userInput, "i");
    } catch (e) {
        // Invalid regex - use an empty regex to match nothing
        userInputRegex = new RegExp("", "i");
    }

    const partsListingTableContent = $('#partsListingTableContent');

    partsListingTableContent.find('tr').each(function() {
        const nameColumnText = $(this).find('td#partName').text();
        const oemColumnText = $(this).find('td#partOem').text();
        const categoryColumnText = $(this).find('td#partCategory').text();
        const carColumnText = $(this).find('td#carBrand').text();
        const combinedText = nameColumnText + " " + oemColumnText + " " + categoryColumnText + " " + carColumnText;
        const hasMatch = userInputRegex.test(combinedText);

        if (!hasMatch && !$(this).hasClass("visually-hidden")) {
            $(this).addClass("visually-hidden");
        } else if (hasMatch && $(this).hasClass("visually-hidden")) {
            $(this).removeClass("visually-hidden");
        }
    });
}

function showPartsListing(result) {
    for (const part of result) {
        let carsString = part.cars.map(car => car.carBrand.brand + ' ' + car.model).join(', ');
        let tableRow = '<tr onclick="location.href=\'part.html?id=' + part.id + '\'">';
        tableRow += "<td>" + part.id + "</td>";
        tableRow += '<td id="partName">' + part.name + "</td>";
        tableRow += '<td id="partOem">' + part.oem + "</td>";
        tableRow += '<td id="partCategory">' + part.category.name + "</td>";
        tableRow += '<td id="carBrand">' + carsString + "</td>";
        tableRow += '<td> <i class="fas fa-chevron-right"></i> </td>';
        tableRow += "</tr>";
        $("#partsListingTableContent").append(tableRow);
    }
}