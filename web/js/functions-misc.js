$(document).ready(function() {

  var colorArray = ['#AA4643', '#4572A7', '#89A54E', '#80699B', '#3D96AE', '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92'];

  var jsonURL = '../data/health-data.json';
  var options = {
    chart: {
      backgroundColor: '#ffffff',
      borderColor: '#a2a2a1',
      borderWidth: 0,
      borderRadius: 0,
      renderTo: 'container',
      type: 'column',
      plotBackgroundColor: '#fffdf6'
    },
    colors: colorArray,
    plotOptions: {
                    column: {
                        stacking: 'normal'
                    }
                },
    title: {
      text: 'Healthy Measures Participants by Zip Code and Gender'
    },
    tooltip: {
      borderRadius: 0,
      borderWidth: 0,
      shadow: false,
      style: {
        fontSize: '7pt',
        color: '#000000'
      }
      // formatter: function() {
      //   return 'Time: ' + this.x + 'Time: ' + (this.y / 60 | 0) + ':' + (this.y % 60 < 10 ? '0' : '') + (this.y % 60);
      // }
    },
    xAxis: {
      categories: [
        '98101','98105','98109'
      ],
      // lineWidth: 1,
      // lineColor: '#333333',
      // minPadding: 0,
      // maxPadding: 0,
      // title: {
      //   text: ''
      // },
      // tickInterval: 2,
      // tickmarkPlacement: 'on'
    },
    yAxis: {
      // gridLineWidth: 0,
      // labels: {
      //   formatter: function() {
      //     return (this.value / 60 | 0) + ':' + (this.value % 60 < 10 ? '0' : '') + (this.value % 60);
      //   },
      //   style: {
      //     color: '#333333'
      //   }
      // },
      // lineWidth: 1,
      // lineColor: '#333333',
      // min: 0,
      // minPadding: 0,
      // maxPadding: 0,
      title: {
        text: ''
      }
    },
    series: [
    ]
  };

  $.getJSON(jsonURL, function(data) {
    $.each(data.data, function(key, val) {
      options.series.push({
        name: val.cat,
        data: val.counts,
        stack: val.stack
      });
    });
    var chart = new Highcharts.Chart(options);
  });
  

/*** Second version ***/

var tArr = [];

  var options2 = {
    chart: {
      renderTo: 'container2',
      type: 'column',
      plotBackgroundColor: '#fffdf6'
    },
    colors: colorArray,
    title: {
      text: 'Healthy Measures Participants by Gender and BMI Level'
    },
    xAxis: {
        categories: [
            'Underweight','Normal','Overweight'
        ]
    },
    yAxis: {
      title: {
        text: 'Participant Count'
      }
    },
    series: [
    ]
  };

  var options3 = {
    chart: {
      renderTo: 'container3',
      type: 'column',
      plotBackgroundColor: '#fffdf6'
    },
    colors: colorArray,
    title: {
      text: 'Healthy Measures Participants - BMI Averages by Age and Gender'
    },
    xAxis: {
        categories: [
            '20-40','40-60','60+'
        ]
    },
    yAxis: {
      title: {
        text: 'Average BMI'
      }
    },
    series: [],
    tooltip: {
        formatter: function() {
            return  '<b>'+this.series.name+' Age Range '+ this.x +
                '</b><br>Average BMI: '+ this.y.toFixed(1) +
                '<br >Count: '+this.point.dataCount;
        }
    }
  };

  var options4 = {
    chart: {
      renderTo: 'container4',
      plotBackgroundColor: '#fffdf6'
    },
    colors: colorArray,
    title: {
      text: 'Healthy Measures Participants - BMI by Age Scatter Plot'
    },
    xAxis: {
      title: {
        text: 'Age'
      }
    },
    yAxis: {
      title: {
        text: 'BMI'
      }
    },
    series: [{
            type: 'scatter',
            name: 'Female',
            data: [

            ]
        },{
            type: 'scatter',
            name: 'Male',
            data: [

            ]
        }],
     plotOptions: {
        scatter: {
            marker: {
                radius: 5,
                states: {
                    hover: {
                        enabled: true,
                        lineColor: 'rgb(100,100,100)'
                    }
                }
            },
            states: {
                hover: {
                    marker: {
                        enabled: false
                    }
                }
            },
            tooltip: {
                valueDecimals: 1,
                headerFormat: '<b>{series.name}</b><br>',
                pointFormat: 'Age: {point.x}<br />BMI: {point.y}'
            }
        }
    }
  };

  var genderM = 0;
  var genderF = 0;
  var mUnder = 0;
  var mNormal = 0;
  var mOver = 0;
  var fUnder = 0;
  var fNormal = 0;
  var fOver = 0;
  var fYoung = 0;
  var fYoungTot = 0;
  var fMed = 0;
  var fMedTot = 0;
  var fOld = 0;
  var fOldTot = 0;
  var mYoung = 0;
  var mYoungTot = 0;
  var mMed = 0;
  var mMedTot = 0;
  var mOld = 0;
  var mOldTot = 0;

  var arrTest = [];
  $.getJSON('../data/health-data-original.json', function(data) {
    $.each(data, function(key, val) {
      
      tArr = {"Age": val.Calculated_Age, "BMI": val.Calculated_BMI};
      if (val.Gender == 'M') {
        genderM++;
        if (val.Calculated_Age < 40) {
            mYoung++;
            mYoungTot = mYoungTot + val.Calculated_BMI;
        } else if (val.Calculated_Age < 60) {
            mMed++;
            mMedTot = mMedTot + val.Calculated_BMI;
        } else {
            mOld++;
            mOldTot = mOldTot + val.Calculated_BMI;
        }

        // for chart2
        if (val.Calculated_BMI < 18.5) {
            mUnder++;
        } else if (val.Calculated_BMI < 25) {
            mNormal++;
        } else {
            mOver++;
        }

        // for chart4
        options4.series[1].data.push([tArr.Age,tArr.BMI]);
      } else {
        genderF++;

        if (val.Calculated_Age < 40) {
            fYoung++;
            fYoungTot = fYoungTot + val.Calculated_BMI;
        } else if (val.Calculated_Age < 60) {
            fMed++;
            fMedTot = fMedTot + val.Calculated_BMI;
        } else {
            fOld++;
            fOldTot = fOldTot + val.Calculated_BMI;
        }

        // for chart2
        if (val.Calculated_BMI < 18.5) {
            fUnder++;
        } else if (val.Calculated_BMI < 25) {
            fNormal++;
        } else {
            fOver++;
        }

        // for chart4
        options4.series[0].data.push([tArr.Age,tArr.BMI]);

      };
    });
    
    options2.series.push({
      name: "Female",
      data: [fUnder,fNormal,fOver]
    },{
      name: "Male",
      data: [mUnder,mNormal,mOver]
    });


    options3.series.push({
      name: "Female",
      data: [{y: (fYoungTot/fYoung), dataCount: fYoung},{y: (fMedTot/fMed), dataCount: fMed},{y: (fOldTot/fOld), dataCount: fOld}]
    },{
      name: "Male",
      data: [{y: (mYoungTot/mYoung), dataCount: mYoung},{y: (mMedTot/mMed), dataCount: mMed},{y: (mOldTot/mOld), dataCount: mOld}]
    });


    var chart2 = new Highcharts.Chart(options2);
    var chart3 = new Highcharts.Chart(options3);
    var chart4 = new Highcharts.Chart(options4);

    // the button handler
    $('#button').click(function() {
        var chartTrend = $('#container4').highcharts();
        if (chartTrend.series.length == 2) {
            chartTrend.addSeries({
                type: 'spline',
                name: 'Trendline - Females',
                marker: { enabled: false },
                /* function returns data for trend-line */
                data: (function() {
                    return fitData(options4.series[0].data, 'exponential').data;
                })()
            });
            chartTrend.addSeries({
                type: 'spline',
                name: 'Trendline - Males',
                marker: { enabled: false },
                /* function returns data for trend-line */
                data: (function() {
                    return fitData(options4.series[1].data, 'exponential').data;
                })()
            });
            console.log(chartTrend.series[2].data);
        }
    });

  });
  

  /*** Baseball example ***/


var optionsBaseball = {
    chart: {
      type: 'column',
      renderTo: 'container-baseball',
    },
    title: {
      text: 'Baseball Players'
    },
    colors: colorArray,
    tooltip: {
      shared: true
    },
    xAxis: {
        categories: [],
        title: {
          text: 'Team'
        }
    },
    yAxis: [{
      title: {
        text: 'Average Age'
      },
      min: 25,
      max: 31,
      tickInterval: 1
    },{ // Secondary yAxis
        title: {
            text: 'Wins'
        },
        opposite: true,
        min: 65,
        max: 100,
        tickInterval: 5
    }],
    series: [{
      name: 'Average Age',
      data: []
    },{
      name: 'Wins',
      yAxis: 1,
      data: []
    }]
    
    
  };

  // Data from http://wiki.stat.ucla.edu/socr/index.php/SOCR_Data_MLB_HeightsWeights
  $.getJSON('../data/baseball-data.json', function(data) {
    var catArray = [];

    var trickyArray = {};
    var longArray = [];
    var nameArray = [];

    $.each(data, function(key, val) {

      // Ver 1 - Basic - make var for each team and add
      if ($.inArray(val.Team, optionsBaseball.xAxis.categories) == -1) {
        //optionsBaseball.xAxis.categories.push(val.Team);  
        catArray.push(val.Team)
        window[val.Team] = 0;
        window[val.Team+"count"] = 0;
      }
      window[val.Team+"count"]++;
      window[val.Team] = window[val.Team] + val.Age;

      // Ver 2 - Put ages in array by team
      var teamName = val.Team;
      if ($.inArray(val.Team, nameArray) == -1) {
        //optionsBaseball.xAxis.categories.push(val.Team);  
        trickyArray[teamName] = { title_name: [teamName], age: [val.Age]};
        nameArray.push(teamName);
      } else {
        trickyArray[teamName].age.push(val.Age);
      }

      // Ver 3 - Just push into array
      longArray.push({ title_name: val.Team, age: val.Age});

    });

    // sorting function
    function SortByName(a, b){
      var aName = a.title_name;
      var bName = b.title_name; 
      return ((aName < bName) ? -1 : ((aName > bName) ? 1 : 0));
    }

    longArray.sort(SortByName);
    //console.log(data);
    //console.log(longArray);

    function parseNow() {

      var allThem = [];
      var goatArray = {};
      $.each(longArray, function(key, val) {

         var teamName = val.title_name;
         if ($.inArray(teamName, allThem) == -1) {
          //optionsBaseball.xAxis.categories.push(val.Team);  
          goatArray[teamName] = { title_name: [teamName], age: [val.age]};
          allThem.push(teamName);
        } else {
          goatArray[teamName].age.push(val.age);
        }      
      });
      return goatArray;
    }
    var pig = parseNow();
    console.log(trickyArray);
    console.log(pig);

    // Take array and push into highcharts
    $.each(pig, function(index,value){
      var total = 0;
      var nameCount = 0;
      var goat = value.age;
      for (var i = 0; i < goat.length; i++) {
        total += goat[i];
        nameCount++;
      }
      var passIt = total / nameCount;
      optionsBaseball.xAxis.categories.push(index);  
      optionsBaseball.series[0].data.push(passIt);
    });

    // teamWins
    // 94,90,84,69,96,85,72,72,96,90,88,73,69,82,71,83,79,88,94,76,89,68,89,71,88,78,66,75,83,73

    optionsBaseball.series[1].data.push(94,90,84,69,96,85,72,72,96,90,88,73,69,82,71,83,79,88,94,76,89,68,89,71,88,78,66,75,83,73);
    console.log(optionsBaseball.series[0].data);
    console.log(optionsBaseball.series[1].data);
    var chartBaseball = new Highcharts.Chart(optionsBaseball);

  });


}); // $(document).ready(function() {