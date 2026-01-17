var stats = {
    type: "GROUP",
name: "All Requests",
path: "",
pathFormatted: "group_missing-name--1146707516",
stats: {
    "name": "All Requests",
    "numberOfRequests": {
        "total": "120",
        "ok": "120",
        "ko": "0"
    },
    "minResponseTime": {
        "total": "18",
        "ok": "18",
        "ko": "-"
    },
    "maxResponseTime": {
        "total": "1005",
        "ok": "1005",
        "ko": "-"
    },
    "meanResponseTime": {
        "total": "118",
        "ok": "118",
        "ko": "-"
    },
    "standardDeviation": {
        "total": "223",
        "ok": "223",
        "ko": "-"
    },
    "percentiles1": {
        "total": "43",
        "ok": "43",
        "ko": "-"
    },
    "percentiles2": {
        "total": "59",
        "ok": "59",
        "ko": "-"
    },
    "percentiles3": {
        "total": "721",
        "ok": "721",
        "ko": "-"
    },
    "percentiles4": {
        "total": "953",
        "ok": "953",
        "ko": "-"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 115,
    "percentage": 96
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 5,
    "percentage": 4
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 0,
    "percentage": 0
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 0,
    "percentage": 0
},
    "meanNumberOfRequestsPerSecond": {
        "total": "10.909",
        "ok": "10.909",
        "ko": "-"
    }
},
contents: {
"req_get-all-product-1342952941": {
        type: "REQUEST",
        name: "Get All Products",
path: "Get All Products",
pathFormatted: "req_get-all-product-1342952941",
stats: {
    "name": "Get All Products",
    "numberOfRequests": {
        "total": "60",
        "ok": "60",
        "ko": "0"
    },
    "minResponseTime": {
        "total": "25",
        "ok": "25",
        "ko": "-"
    },
    "maxResponseTime": {
        "total": "953",
        "ok": "953",
        "ko": "-"
    },
    "meanResponseTime": {
        "total": "102",
        "ok": "102",
        "ko": "-"
    },
    "standardDeviation": {
        "total": "201",
        "ok": "201",
        "ko": "-"
    },
    "percentiles1": {
        "total": "44",
        "ok": "44",
        "ko": "-"
    },
    "percentiles2": {
        "total": "59",
        "ok": "59",
        "ko": "-"
    },
    "percentiles3": {
        "total": "567",
        "ok": "567",
        "ko": "-"
    },
    "percentiles4": {
        "total": "947",
        "ok": "947",
        "ko": "-"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 57,
    "percentage": 95
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 3,
    "percentage": 5
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 0,
    "percentage": 0
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 0,
    "percentage": 0
},
    "meanNumberOfRequestsPerSecond": {
        "total": "5.455",
        "ok": "5.455",
        "ko": "-"
    }
}
    },"req_get-product-by--1691903849": {
        type: "REQUEST",
        name: "Get Product By ID",
path: "Get Product By ID",
pathFormatted: "req_get-product-by--1691903849",
stats: {
    "name": "Get Product By ID",
    "numberOfRequests": {
        "total": "60",
        "ok": "60",
        "ko": "0"
    },
    "minResponseTime": {
        "total": "18",
        "ok": "18",
        "ko": "-"
    },
    "maxResponseTime": {
        "total": "1005",
        "ok": "1005",
        "ko": "-"
    },
    "meanResponseTime": {
        "total": "134",
        "ok": "134",
        "ko": "-"
    },
    "standardDeviation": {
        "total": "242",
        "ok": "242",
        "ko": "-"
    },
    "percentiles1": {
        "total": "43",
        "ok": "43",
        "ko": "-"
    },
    "percentiles2": {
        "total": "58",
        "ok": "58",
        "ko": "-"
    },
    "percentiles3": {
        "total": "721",
        "ok": "721",
        "ko": "-"
    },
    "percentiles4": {
        "total": "973",
        "ok": "973",
        "ko": "-"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 58,
    "percentage": 97
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 2,
    "percentage": 3
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 0,
    "percentage": 0
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 0,
    "percentage": 0
},
    "meanNumberOfRequestsPerSecond": {
        "total": "5.455",
        "ok": "5.455",
        "ko": "-"
    }
}
    }
}

}

function fillStats(stat){
    $("#numberOfRequests").append(stat.numberOfRequests.total);
    $("#numberOfRequestsOK").append(stat.numberOfRequests.ok);
    $("#numberOfRequestsKO").append(stat.numberOfRequests.ko);

    $("#minResponseTime").append(stat.minResponseTime.total);
    $("#minResponseTimeOK").append(stat.minResponseTime.ok);
    $("#minResponseTimeKO").append(stat.minResponseTime.ko);

    $("#maxResponseTime").append(stat.maxResponseTime.total);
    $("#maxResponseTimeOK").append(stat.maxResponseTime.ok);
    $("#maxResponseTimeKO").append(stat.maxResponseTime.ko);

    $("#meanResponseTime").append(stat.meanResponseTime.total);
    $("#meanResponseTimeOK").append(stat.meanResponseTime.ok);
    $("#meanResponseTimeKO").append(stat.meanResponseTime.ko);

    $("#standardDeviation").append(stat.standardDeviation.total);
    $("#standardDeviationOK").append(stat.standardDeviation.ok);
    $("#standardDeviationKO").append(stat.standardDeviation.ko);

    $("#percentiles1").append(stat.percentiles1.total);
    $("#percentiles1OK").append(stat.percentiles1.ok);
    $("#percentiles1KO").append(stat.percentiles1.ko);

    $("#percentiles2").append(stat.percentiles2.total);
    $("#percentiles2OK").append(stat.percentiles2.ok);
    $("#percentiles2KO").append(stat.percentiles2.ko);

    $("#percentiles3").append(stat.percentiles3.total);
    $("#percentiles3OK").append(stat.percentiles3.ok);
    $("#percentiles3KO").append(stat.percentiles3.ko);

    $("#percentiles4").append(stat.percentiles4.total);
    $("#percentiles4OK").append(stat.percentiles4.ok);
    $("#percentiles4KO").append(stat.percentiles4.ko);

    $("#meanNumberOfRequestsPerSecond").append(stat.meanNumberOfRequestsPerSecond.total);
    $("#meanNumberOfRequestsPerSecondOK").append(stat.meanNumberOfRequestsPerSecond.ok);
    $("#meanNumberOfRequestsPerSecondKO").append(stat.meanNumberOfRequestsPerSecond.ko);
}
