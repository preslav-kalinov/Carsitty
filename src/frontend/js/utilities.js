let currentUserRole;

$.ajax({
    type: 'GET',
    url: APICONFIG.host + '/me',
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true,
    success: function(result) {
        showLoggedUserInfo(result)
        currentUserRole = result.role;
    },
    error: function(xhr, status, code) {
        window.location.href = "../login.html";
    }
});

function onPageLoaded() {
    showElement("#returnToAdminMenuContainer");
    showElement("#createBackupContainer");

    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/parts/logs',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            hideElement("#loadingContainer");
            hideElement("#errorMessageContainer");

            showElement("#logsListingContainer");
            showLogData(result);
        },
        error: function(xhr, status, code) {
            hideElement("#loadingContainer");
            showElement("#errorMessageContainer");
            hideElement('#logsListingContainer');
            hideElement("#createBackupContainer");
            hideElement("#logSearchField")

            if (xhr.status == 403) {
                hideElement("#returnToAdminMenuContainer");

                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                return;
            }
            
            if (xhr.status == 404 || xhr.status == 401) {
                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                return;
            }

            $("#errorMessageContent").append("Cannot connect to server");
        }
    });
}

function logSearchInputChanged() {
    const userInput = $("#logSearch").val().trim();
    let userInputRegex;

    try {
        userInputRegex = new RegExp(userInput, "i");
    } catch (e) {
        //empty regex to match nothing
        userInputRegex = new RegExp("", "i");
    }

    const logsListingTableContent = $('#logsListingTableContent');

    logsListingTableContent.find('tr').each(function() {
        const eventTimeColumnText = $(this).find('td#eventTime').text();
        const logMessageColumnText = $(this).find('td#logMessage').text();
        const combinedText = eventTimeColumnText + " " + logMessageColumnText;

        const hasMatch = userInputRegex.test(combinedText);

        if (!hasMatch && !$(this).hasClass("visually-hidden")) {
            $(this).addClass("visually-hidden");
        } else if (hasMatch && $(this).hasClass("visually-hidden")) {
            $(this).removeClass("visually-hidden");
        }
    });
}

function showLogData(result) {
    result.sort((a, b) => new Date(b.incidentTime) - new Date(a.incidentTime));

    for(const log of result) {
        let tableRow = "<tr>";
        tableRow += '<td id="eventTime">' + log.incidentTime + "</td>";
        tableRow += '<td id="logMessage">' + log.message + "</td>";
        tableRow += "</tr>";

        $("#logsListingTableContent").append(tableRow);
    }
}

function changeBackupeModal() {
    $("#createBackupModalBody").text("Are you sure you want to create backup of the database?");
    $("#createBackupModalYesButton").attr("onclick", "createBackup()");
}

function createBackup() {
    $.ajax({
        type: 'POST',
        url: APICONFIG.host + '/backup',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            $("#successMessageContent").text("Database backup was created successfully on host '" + new URL(APICONFIG.host).hostname + "'")
            showElement("#successMessageContainer");
            $("#createBackupModalNoButton").click();
        },
        error: function (xhr, status, code) {
            $("#createBackupModalNoButton").click();
            showElement("#errorMessageContainer");
            if(xhr.status == 404 || xhr.status == 400 || xhr.status == 401 || xhr.status == 403) {
                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                return;
            }
            $("#errorMessageContent").append("Cannot connect to server");
        }
    });
}