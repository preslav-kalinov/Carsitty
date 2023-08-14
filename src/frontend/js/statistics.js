let charts = [];

$.ajax({
    type: 'GET',
    url: APICONFIG.host + '/me',
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true,
    success: function(result) {
        showLoggedUserInfo(result);
    },
    error: function(xhr, status, code) {
        window.location.href = "../login.html";
    }
});

function onPageLoaded() {
    hideElement("#loadingContainer");
    showElement("#returnToManagerMenuContainer");

    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/sales',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            hideElement("#loadingContainer");
            hideElement("#errorMessageContainer");
            parseSalesData(result);
        },
        error: function(xhr, status, code) {
            hideElement("#loadingContainer");
            showElement("#errorMessageContainer");

            if (xhr.status == 403) {
                hideElement("#returnToManagerMenuContainer");
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

function parseSalesData(sales) {
    const partSales = {}; 
    const partProfits = {}; 
    const partQuantities = {};
    const employeeSales = {};
    
    let partNames = {}; 
    let employeeNames = {};

    for (sale of sales) {
        const partId = sale.part.id;
        const userId = sale.user.id;

        if (!partSales[partId]) {
            partSales[partId] = sale.soldQuantity;
        } else {
            partSales[partId] += sale.soldQuantity;
        }

        if (!partProfits[partId]) {
            partProfits[partId] = sale.saleProfit;
        } else {
            partProfits[partId] += sale.saleProfit;
        }

        if (!partQuantities[partId]) {
            partQuantities[partId] = sale.part.quantity;
        } else {
            partQuantities[partId] += sale.part.quantity;
        }

        if (!partNames[partId]) {
            partNames[partId] = sale.part.name;
        }

        if (sale.user.role.role === "Employee") {
            if (!employeeSales[userId]) {
            employeeSales[userId] = 1;
            } else {
            employeeSales[userId] += 1;
            }

            if (!employeeNames[userId]) {
                employeeNames[userId] = sale.user.displayName;
            }
        }
    }

    const finalMostSoldPartPieChartData = Object.entries(partSales).sort((a, b) => b[1] - a[1]);
    const finalMostProfitPartPieChartData = Object.entries(partProfits).sort((a, b) => b[1] - a[1]);
    const finalMostQuantityPartPieChartData = Object.entries(partQuantities).sort((a, b) => b[1] - a[1]);
    const finalEmployeeSalesChartData = Object.entries(employeeSales).sort((a, b) => b[1] - a[1]);

    let mostSoldPartLabels = [];
    let mostSoldPartValues = []; 
    let mostSoldPartBackgroundColors = []; 

    let otherSoldPartsSum = 0;
    for (let i = 0; i < finalMostSoldPartPieChartData.length; i++) {
        if (i < 2) {
            mostSoldPartLabels.push(partNames[finalMostSoldPartPieChartData[i][0]]);
            mostSoldPartValues.push(finalMostSoldPartPieChartData[i][1]);
            mostSoldPartBackgroundColors.push(i === 0 ? "#2673F2" : "#EBB000");
        } else {
            otherSoldPartsSum += finalMostSoldPartPieChartData[i][1];
        }
    }

    if (otherSoldPartsSum) {
        mostSoldPartLabels.push("Other parts");
        mostSoldPartValues.push(otherSoldPartsSum);
        mostSoldPartBackgroundColors.push("#28A745");
    }

    let sumMostProfitPartLabels = []; 
    let sumMostProfitPartValues = []; 
    let sumMostProfitPartBackgroundColors = []; 

    let otherProfitPartsSum = 0;
    for (let i = 0; i < finalMostProfitPartPieChartData.length; i++) {
        if (i < 2) {
            sumMostProfitPartLabels.push(partNames[finalMostProfitPartPieChartData[i][0]]);
            sumMostProfitPartValues.push(finalMostProfitPartPieChartData[i][1]);
            sumMostProfitPartBackgroundColors.push(i === 0 ? "#2673F2" : "#EBB000");
        } else {
            otherProfitPartsSum += finalMostProfitPartPieChartData[i][1];
        }
    }

    if (otherProfitPartsSum) {
        sumMostProfitPartLabels.push("Other parts");
        sumMostProfitPartValues.push(otherProfitPartsSum);
        sumMostProfitPartBackgroundColors.push("#28A745");
    }

    let mostQuantityPartLabels = []; 
    let mostQuantityPartValues = []; 
    let sumMostQuantityPartBackgroundColors = []; 

    let otherQuantityPartsSum = 0;
    for (let i = 0; i < finalMostQuantityPartPieChartData.length; i++) {
        if (i < 2) {
            mostQuantityPartLabels.push(partNames[finalMostQuantityPartPieChartData[i][0]]);
            mostQuantityPartValues.push(finalMostQuantityPartPieChartData[i][1]);
            sumMostQuantityPartBackgroundColors.push(i === 0 ? "#2673F2" : "#EBB000");
        } else {
            otherQuantityPartsSum += finalMostQuantityPartPieChartData[i][1];
        }
    }

    if (otherQuantityPartsSum) {
        mostQuantityPartLabels.push("Other parts");
        mostQuantityPartValues.push(otherQuantityPartsSum);
        sumMostQuantityPartBackgroundColors.push("#28A745");
    }

    let employeeLabels = [];
    let employeeValues = [];
    let employeeBackgroundColors = ["#2673F2", "#EBB000", "#28A745"];

    for (let i = 0; i < Math.min(finalEmployeeSalesChartData.length, 3); i++) {
        employeeLabels.push(employeeNames[finalEmployeeSalesChartData[i][0]]);
        employeeValues.push(finalEmployeeSalesChartData[i][1]);
    }

    if (employeeLabels.length == 3) {
        generateBarChart("topEmployeesChart", "Top Employees by Sales", employeeLabels, employeeValues, employeeBackgroundColors);
    } else {
        hideElement("#topEmployeesChart");
    }

    generatePieChart("mostSoldPartsChart", "Most Parts Sold", mostSoldPartLabels, mostSoldPartValues, mostSoldPartBackgroundColors);
    generatePieChart("mostOverallProfitPartsChart", "Overall Parts Profit", sumMostProfitPartLabels, sumMostProfitPartValues, sumMostProfitPartBackgroundColors);
    generatePieChart("mostQuantityPartsChart", "Most Parts Quantity", mostQuantityPartLabels, mostQuantityPartValues, sumMostQuantityPartBackgroundColors);
}

function generatePieChart(chartId, title, labels, values, backgroundColors) {
    return new Chart(document.getElementById(chartId).getContext('2d'), {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: backgroundColors,
                hoverBackgroundColor: "#A8B3C5"
            }]
        },
        options: {
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(oTooltipContext) {
                            return oTooltipContext.formattedValue + (chartId == "mostOverallProfitPartsChart" ? " BGN" : " parts");
                        }
                    }
                },
                legend: {
                    labels: {
                        color: "#E5E5E5",
                    }
                },
                title: {
                    display: true,
                    text: title,
                    color: "#E5E5E5",
                    font: {
                        size: 26,
                    }
                }
            },
            responsive: true
        }
    });
}

function generateBarChart(chartId, title, labels, values, backgroundColors) {
    new Chart(document.getElementById(chartId).getContext('2d'), {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: title,
                data: values,
                backgroundColor: backgroundColors,
            }]
        },
        options: {
            aspectRatio: 0.6,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                title: {
                    display: true,
                    text: title,
                    color: "#E5E5E5",
                    font: {
                        size: 26,
                    },
                    padding: {
                        bottom: 40
                    }
                },
                tooltip: {
                    displayColors: false,
                    callbacks: {
                        label: function(oTooltipContext) {
                            return oTooltipContext.formattedValue + " sales";
                        },
                    }
                }
            },
            responsive: true,
            scales: {
                x: {
                    ticks: {
                        color: 'white'
                    },
                    grid: {
                        display: false
                    },
                    border: {
                        display: true,
                        color: 'rgba(255, 255, 255, 0.5)'
                    }
                },
                y: {
                    display: false
                }
            }
        }
    });
}