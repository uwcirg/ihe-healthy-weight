$(document).ready(function() {

  // Quick way of building the main nav and highlighting the active link
  var navLinks = {
    index: "Obesity",
    individual: "Individual",
    exercise: "Excercise",    
    geo: "Washington State",
    occupation: "Occupation",
    himss: "Showcase",
    links: "Links"
  };
  $.each(navLinks, function(key, val) {
    var insertLink = '<li><a href="' + key + '.html">' + val + '</a></li>';
    $("#mainNav").append(insertLink);
  });
  var url = window.location.pathname,
          urlRegExp = new RegExp(url.replace(/\/$/, ''));
  // now grab every link from the navigation
  if (url == '/') {
    $('#mainNav li').first().addClass('active');
  } else {
    $('#mainNav li a').each(function() {
      // and test its href against the url pathname regexp
      if (urlRegExp.test(this.href)) {
        $(this).parent().addClass('active');
      }
    });    
  }

  // Get string from URL, used for occupation select charts
  function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
      var pair = vars[i].split("=");
      if (pair[0] == variable) {
        return pair[1];
      }
    }
    return(false);
  }

  // Names of occupation_23. 
  var ocNames = {
    11: "Management (11)",
    13: "Business and Financial Operations Occupations (13)",
    15: "Computer and Mathematical Occupations (15)",
    17: "Architecture and Engineering Occupations (17)",
    19: "Life, Physical, and Social Science Occupations",
    23: "Legal Occupations",
    25: "Education, Training, and Library Occupations",
    27: "Arts, Design, Entertainment, Sports, and Media Occupations",
    29: "Healthcare practitioners and technical (29)",
    31: "Healthcare Support Occupations",
    33: "Protective Service Occupations",
    35: "Food Preparation and Serving Related Occupations",
    37: "Building and Grounds Cleaning and Maintenance Occupations",
    39: "Personal Care and Service Occupations",
    41: "Sales and Related Occupations",
    43: "Office and Administrative Support Occupations",
    45: "Farming, Fishing, and Forestry Occupations",
    47: "Construction and Extraction Occupations",
    49: "Installation, Maintenance, and Repair Occupations",
    51: "Production Occupations",
    53: "Transportation and Material Moving Occupations"
  };
  var selectedOc = getQueryVariable('occupation');
  var hasOc = (selectedOc) ? true : false;
  var passedOccupation = false;
  // Change select value for occupation to match URL string
  if (hasOc) {
    $("#occupationSelect").val(selectedOc);
    passedOccupation = ocNames[selectedOc];
  }
  $("#occupationSelect").on('change', function() {
    var chosen = $(this).val();
    if (chosen) {
      var currentPage = window.location.href.substr(0, window.location.href.indexOf('?'));
      window.location.href = currentPage + "?occupation=" + chosen;
    }
  });

  // Highcharts options and defaults
  var colorArray = ['#AA4643', '#4572A7', '#89A54E', '#80699B', '#3D96AE', '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92'];
  var color3 = ['#cc0000', '#cc6600', '#009900'];
  var color3more = ['#cc0000', '#cc6600', '#009900', '#cc5252', '#CC8F52', '#3D993D'];
  var colorPinkBlue = ['#C4289E', '#F733C7', '#F87EDA', '#3637C4', '#4445F7', '#8F90F8'];
  Highcharts.setOptions({
    credits: {
      enabled: false
    },
    chart: {
      events: {
        load: function(event) {
          $("#loadingImage").fadeOut();
        }
      }  
    },
    title: {
      style: {
        color: '#333',
        fontSize: '24px'
      }
    },
    subtitle: {
        style: {
            fontSize: '18px'
        }
    },    
    legend: {
      itemStyle: {
        fontSize: '14px'
      }
    },
    tooltip: {
      headerFormat: '<span style="font-size: 14px"><b>{point.key}</b></span><br/>',
      backgroundColor: '#FCFFC5',
      style: {
        fontSize: '14px'
      }
    },
    xAxis: {
      labels: {
        y: 18,
        style: {
          fontSize: '14px'
        }
      }
    },
    yAxis: {
      labels: {
        style: {
          fontSize: '14px'
        }
      },
      title: {
        style: {
          fontSize: '14px'
        }
      }
    }    
  });

  var options1 = {
    chart: {
      renderTo: 'container1',
      type: 'column',
      plotBackgroundColor: '#fffdf6'
    },
    legend: {
      reversed: true
    },
    colors: color3,
    title: {
      text: 'BMI category by Age Group'
    },
    xAxis: {
      categories: [
        'Under 20', '20-40', '40-60', '60+'
      ]
    },
    yAxis: {
      title: {
        text: 'Percentage'
      }
    },
    series: [],
    plotOptions: {
      column: {
        stacking: 'percent'
      }
    },
    tooltip: {
      pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.percentage:.0f}%<br/>',
      shared: true
    }
  };

  var options2 = {
    chart: {
      renderTo: 'container2',
      type: 'scatter',
      zoomType: 'xy',
      plotBackgroundColor: '#fffdf6'
    },
    colors: colorArray,
    title: {
      text: 'Dashboard 2 - BMI vs Age'
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
        name: 'Female',
        color: '#ff99ff',
        data: []
      }, {
        name: 'Male',
        color: '#3399ff',
        data: []
      }],
    tooltip: {
      headerFormat: '<b>{series.name}</b><br>',
      pointFormat: 'Age: {point.x} - BMI: {point.y}'
    }
  };

  var options3 = {
    chart: {
      renderTo: 'container3',
      type: 'column'
    },
    legend: {
      itemWidth: 170,
      width: 510
    },
    colors: colorPinkBlue,
    title: {
      text: 'BMI by Age Group and Sex'
    },
    xAxis: {
      categories: [
        '0-19', '20-39', '40-59', '60+'
      ]
    },
    yAxis: {
      title: {
        text: 'Percentage'
      }
    },
    series: [],
    plotOptions: {
      column: {
        stacking: 'percent'
      }
    },
    tooltip: {
      pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.percentage:.0f}%<br/>'
    }
  };

  var options4 = {
    chart: {
      renderTo: 'container4',
      type: 'column'
    },
    legend: {
      reversed: true
    },
    colors: color3,
    title: {
      text: 'BMI for ' + passedOccupation + ' vs Overall'
    },
    xAxis: {
      categories: [
        passedOccupation, 'All'
      ]
    },
    yAxis: {
      title: {
        text: 'Percentage'
      }
    },
    series: [],
    plotOptions: {
      column: {
        stacking: 'percent'
      }
    },
    tooltip: {
      pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.percentage:.0f}%<br/>',
      shared: true
    }
  };

  var options5 = {
    chart: {
      renderTo: 'container5',
      type: 'area'
    },
    colors: color3,
    title: {
      text: 'BMI Percentage Over Time'
    },
    xAxis: {
      categories: ['2009', '2010', '2011', '2012', '2013', '2014']
    },
    yAxis: {
      title: {
        text: 'Percentage'
      }
    },
    tooltip: {
      pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.percentage:.1f}%<br/>',
      shared: true
    },
    plotOptions: {
      area: {
        stacking: 'percent',
        lineColor: '#ffffff',
        lineWidth: 1,
        marker: {
          lineWidth: 1,
          lineColor: '#ffffff'
        }
      }
    },
    series: []
  };

  var options6 = {
    chart: {
      renderTo: 'container6',
      type: 'line'
    },
    colors: colorArray,
    title: {
      text: 'BMI Average Over Time'
    },
    tooltip: {
      valueDecimals: 2,
      shared: true,
      crosshairs: true
    },
    xAxis: {
      categories: ['2009', '2010', '2011', '2012', '2013', '2014']
    },
    series: []
  };

  var options7 = {
    chart: {
      renderTo: 'container7',
      type: 'column'
    },
    legend: {
      reversed: true
    },
    colors: color3,
    title: {
      text: 'BMI by Occupation Group'
    },
    xAxis: {
      categories: [
      ]
    },
    yAxis: {
      title: {
        text: 'Percentage'
      }
    },
    series: [{
        name: 'Obese',
        data: []
      }, {
        name: 'Overweight',
        data: []
      }, {
        name: 'Normal',
        data: []
      }],
    plotOptions: {
      column: {
        stacking: 'percent'
      }
    },
    tooltip: {
      pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.percentage:.0f}%<br/>',
      shared: true
    }
  };

  var options8 = {
    chart: {
      renderTo: 'container8',
      type: 'column',
      events: {
        load: function(event) {
          $("#occupationBox").fadeIn();
        }
      }
    },
    colors: colorArray,
    title: {
      text: 'Percent Obese by Occupation Group'
    },
    xAxis: {
      categories: []
    },
    yAxis: {
      title: {
        text: 'Percent Obese'
      },
      labels: {
        formatter: function() {
          return this.value + '%';
        }
      }
    },
    series: [{
        name: 'Female',
        data: []
      }, {
        name: 'Male',
        data: []
      }],
    tooltip: {
      valueDecimals: 0,
      pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.y}%<br/>'
    }
  };

  var options9 = {
    chart: {
      renderTo: 'container9',
      type: 'column'
    },
    legend: {
    },
    colors: colorArray,
    title: {
      text: 'Percent Obese for Specific Occupation Group'
    },
    xAxis: {
      categories: []
    },
    yAxis: {
      title: {
        text: 'Percent Obese'
      },
      labels: {
        formatter: function() {
          return this.value + '%';
        }
      }
    },
    series: [{
        name: 'Female',
        data: []
      }, {
        name: 'Male',
        data: []
      }],
    tooltip: {
      valueDecimals: 0,
      pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.y}%<br/>'
    }
  };

  var options10 = {
    chart: {
      renderTo: 'container10',
      type: 'boxplot'
    },
    colors: colorArray,
    title: {
      text: 'Boxplot - Median BMI by Occupation'
    },
    xAxis: {
      categories: []
    },
    tooltip: {
      useHTML: true,
      headFormat: '{point.key} - {series.name}',
      pointFormat: '95th quantile: {point.high}<br />Upper quartile: {point.q3}<br />Median: {point.median}<br />Lower quartile: {point.q1}<br />5th quantile: {point.low}'
    },
    yAxis: {
      title: {
        text: 'BMI'
      },
      min: 18,
      max: 42,
      startOnTick: false,
      endOnTick: false,
      plotLines: [{
          color: '#E47400',
          value: 25,
          width: 3,
          label: {
            text: 'Overweight',
            align: 'left',
            style: {
              color: '#E47400'
            }

          }
        }, {
          color: '#f08080',
          value: 30,
          width: 3,
          label: {
            text: 'Obese',
            align: 'left',
            style: {
              color: '#f08080'
            }
          }
        }]
    },
    series: [{
        name: 'Occupation',
        data: []
      }]
  };

  var options11 = {
    chart: {
      renderTo: 'container11',
      type: 'column'
    },
    legend: {
      width: 540,
      itemWidth: 180
    },
    colors: colorPinkBlue,
    title: {
      text: 'Exercise Level by Age Group and Sex'
    },
    xAxis: {
      categories: []
    },
    yAxis: {
      title: {
        text: 'Percentage'
      }
    },
    series: [{
        name: 'Female - Insufficient',
        data: [],
        stack: "Female"
      }, {
        name: 'Female - Adequate',
        data: [],
        stack: "Female"
      }, {
        name: 'Female - Extensive',
        data: [],
        stack: "Female"
      }, {
        name: 'Male - Insufficient',
        data: [],
        stack: "Male"
      }, {
        name: 'Male - Adequate',
        data: [],
        stack: "Male"
      }, {
        name: 'Male - Extensive',
        data: [],
        stack: "Male"
      }],
    plotOptions: {
      column: {
        stacking: 'percent'
      }
    },
    tooltip: {
      pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.percentage:.0f}%<br/>'
    }
  };

  var options12 = {
    chart: {
      renderTo: 'container12',
      type: 'boxplot'
    },
    legend: {
    },
    title: {
      text: 'Your BMI & Exercise Level Compared to Other Woman Ages 20-49'
    },
    subtitle: {
      text: 'Median BMI Boxplot for different levels of exercise.'
    },
    xAxis: {
      categories: []
    },
    tooltip: {
      useHTML: true,
      headFormat: '{point.key} - {series.name}',
      pointFormat: '95th quantile: {point.high}<br />Upper quartile: {point.q3}<br />Median: {point.median}<br />Lower quartile: {point.q1}<br />5th quantile: {point.low}'
    },
    yAxis: {
      title: {
        text: 'BMI'
      },
      min: 18,
      max: 43,
      startOnTick: false,
      endOnTick: false,
      plotLines: [{
          color: '#E47400',
          value: 25,
          width: 2,
          label: {
            text: 'Overweight',
            align: 'left',
            style: {
              color: '#E47400'
            }

          }
        }, {
          color: '#f08080',
          value: 30,
          width: 2,
          label: {
            text: 'Obese',
            align: 'left',
            style: {
              color: '#f08080'
            }
          }
        }]
    },
    series: [{
        name: 'Exercise Level ',
        data: []
      }, {
        name: 'Your BMI & Exercise Level',
        color: Highcharts.getOptions().colors[2],
        type: 'scatter',
        data: [// x, y positions where 0 is the first category
          [1, 27.4]
        ],
        marker: {
          fillColor: Highcharts.getOptions().colors[2],
          radius: 10
        },
        tooltip: {
          pointFormat: 'Your BMI: {point.y}<br />Your exercise level is adequate.'
        }
      }]
  };
  
  // option1 calculations
  var age1n = 0,
          age1o = 0,
          age1b = 0,
          age2n = 0,
          age2o = 0,
          age2b = 0,
          age3n = 0,
          age3o = 0,
          age3b = 0,
          age4n = 0,
          age4o = 0,
          age4b = 0;
  // option3 calculations
  var age1nf = 0,
          age1of = 0,
          age1bf = 0,
          age2nf = 0,
          age2of = 0,
          age2bf = 0,
          age3nf = 0,
          age3of = 0,
          age3bf = 0,
          age4nf = 0,
          age4of = 0,
          age4bf = 0;
  var age1nm = 0,
          age1om = 0,
          age1bm = 0,
          age2nm = 0,
          age2om = 0,
          age2bm = 0,
          age3nm = 0,
          age3om = 0,
          age3bm = 0,
          age4nm = 0,
          age4om = 0,
          age4bm = 0;

  // option4 vars
  var ocSeln = 0,
          ocSelo = 0,
          ocSelb = 0,
          ocAlln = 0,
          ocAllo = 0,
          ocAllb = 0;

  // options5 vars
  var n08 = 0,
          o08 = 0,
          b08 = 0,
          v08 = 0,
          s08 = 0,
          s08c = 0,
          n09 = 0,
          o09 = 0,
          b09 = 0,
          v09 = 0,
          s09 = 0,
          s09c = 0,
          n10 = 0,
          o10 = 0,
          b10 = 0,
          v10 = 0,
          s10 = 0,
          s10c = 0,
          n11 = 0,
          o11 = 0,
          b11 = 0,
          v11 = 0,
          s11 = 0,
          s11c = 0,
          n12 = 0,
          o12 = 0,
          b12 = 0,
          v12 = 0,
          s12 = 0,
          s12c = 0,
          n13 = 0,
          o13 = 0,
          b13 = 0,
          v13 = 0,
          s13 = 0,
          s13c = 0,
          n14 = 0,
          o14 = 0,
          b14 = 0,
          v14 = 0,
          s14 = 0,
          s14c = 0;
  
  // Arrays used for building each chart
  var opt7Array = [];
  var opt8Array = [];
  var opt9Array = [];
  var opt10Array = [];
  var opt11Array = [];
  var opt12Array = [];

  // Helper functions
  // Do alphabetical sort - used in chart categories
  function SortByName(a, b) {
    var aName = a.toLowerCase();
    var bName = b.toLowerCase();
    return ((aName < bName) ? -1 : ((aName > bName) ? 1 : 0));
  }
  
  // Get median value for data set.
  function median(values) {

    var minSet;
    var lowQ;
    var medianSet;
    var highQ;
    var maxSet;

    values.sort(function(a, b) {
      return a - b;
    });
    var half = Math.floor(values.length / 2);
    var low = Math.floor(values.length / 4);
    var high = Math.floor(3 * (values.length / 4));
    var low5 = Math.floor(values.length / 20);
    var high5 = Math.floor(19 * (values.length / 20));

    if (values.length % 2) {
      medianSet = values[half];
      lowQ = (values[low - 1] + values[low]) / 2.0;
      highQ = (values[high - 1] + values[high]) / 2.0;
      minSet = values[low5];
      maxSet = values[high5];
    } else {
      medianSet = (values[half - 1] + values[half]) / 2.0;
      lowQ = values[low];
      highQ = values[high];
      minSet = (values[low5 - 1] + values[low5]) / 2.0;
      maxSet = (values[high5 - 1] + values[high5]) / 2.0;
    }

    var returnSet = [minSet, lowQ, medianSet, highQ, maxSet];
    return returnSet;
  }

  // Builder functions called for each value in json
  function buildPercents(toAdd, theArray, bmi, gender) {
    var checkIt = $.inArray(toAdd, theArray);
    if (checkIt == -1) {
      theArray.push(toAdd);
      theArray[toAdd] = [];
      theArray[toAdd].b = 0;
      theArray[toAdd].o = 0;
      theArray[toAdd].n = 0;
      if (gender) {
        theArray[toAdd].bm = 0;
        theArray[toAdd].om = 0;
        theArray[toAdd].nm = 0;
      }
    }
    if (bmi >= 30) {
      if (gender == 'M') {
        theArray[toAdd].bm++;
      } else {
        theArray[toAdd].b++;
      }
    } else if (bmi >= 25) {
      if (gender == 'M') {
        theArray[toAdd].om++;
      } else {
        theArray[toAdd].o++;
      }
    } else {
      if (gender == 'M') {
        theArray[toAdd].nm++;
      } else {
        theArray[toAdd].n++;
      }
    }
  }

  function buildBoxplot(toAdd, theArray, bmi) {
    var checkIt = $.inArray(toAdd, theArray);
    if (checkIt == -1) {
      theArray.push(toAdd);
      theArray[toAdd] = [];
    }
    theArray[toAdd].push(bmi);
  }

  function buildExercise(toAdd, theArray, physical, gender) {
    var checkIt = $.inArray(toAdd, theArray);
    if (checkIt == -1) {
      theArray.push(toAdd);
      theArray[toAdd] = [];
      theArray[toAdd].i = 0;
      theArray[toAdd].a = 0;
      theArray[toAdd].e = 0;
      if (gender) {
        theArray[toAdd].im = 0;
        theArray[toAdd].am = 0;
        theArray[toAdd].em = 0;
      }
    }
    if (physical == 1) {
      if (gender == 'M') {
        theArray[toAdd].im++;
      } else {
        theArray[toAdd].i++;
      }
    } else if (physical == 2) {
      if (gender == 'M') {
        theArray[toAdd].am++;
      } else {
        theArray[toAdd].a++;
      }
    } else {
      if (gender == 'M') {
        theArray[toAdd].em++;
      } else {
        theArray[toAdd].e++;
      }
    }
  }

  $.getJSON('../data/visits_Feb_23a.json', function(data) {

    $.each(data, function(key, val) {

      // option1 calcs
      if (val.calculated_age > 60) {
        if (val.calculated_bmi >= 30) {
          age4b++
          if (val.gender == 'F') {
            age4bf++
          } else {
            age4bm++
          }
        } else if (val.calculated_bmi >= 25) {
          age4o++
          if (val.gender == 'F') {
            age4of++
          } else {
            age4om++
          }
        } else {
          age4n++
          if (val.gender == 'F') {
            age4nf++
          } else {
            age4nm++
          }
        }
      } else if (val.calculated_age > 40) {
        if (val.calculated_bmi >= 30) {
          age3b++
          if (val.gender == 'F') {
            age3bf++
          } else {
            age3bm++
          }
        } else if (val.calculated_bmi >= 25) {
          age3o++
          if (val.gender == 'F') {
            age3of++
          } else {
            age3om++
          }
        } else {
          age3n++
          if (val.gender == 'F') {
            age3nf++
          } else {
            age3nm++
          }
        }
      } else if (val.calculated_age > 20) {
        if (val.calculated_bmi >= 30) {
          age2b++
          if (val.gender == 'F') {
            age2bf++
          } else {
            age2bm++
          }
        } else if (val.calculated_bmi >= 25) {
          age2o++
          if (val.gender == 'F') {
            age2of++
          } else {
            age2om++
          }
        } else {
          age2n++
          if (val.gender == 'F') {
            age2nf++
          } else {
            age2nm++
          }
        }
      } else {
        if (val.calculated_bmi >= 30) {
          age1b++
          if (val.gender == 'F') {
            age1bf++
          } else {
            age1bm++
          }
        } else if (val.calculated_bmi >= 25) {
          age1o++
          if (val.gender == 'F') {
            age1of++
          } else {
            age1om++
          }
        } else {
          age1n++
          if (val.gender == 'F') {
            age1nf++
          } else {
            age1nm++
          }
        }
      }

      // option4 calc
      if (hasOc) {
        if (val.calculated_bmi >= 30) {
          if (val.occupation_23 == selectedOc) {
            ocSelb++
          }
          ocAllb++;
        } else if (val.calculated_bmi >= 25) {
          if (val.occupation_23 == selectedOc) {
            ocSelo++
          }
          ocAllo++;
        } else {
          if (val.occupation_23 == selectedOc) {
            ocSeln++
          }
          ocAlln++;
        }
      }

      // option5 calc
      var obsDate = new Date(val.obs_date);
      var obsYear = obsDate.getFullYear();
      if (obsYear == 2009) {
        v09 += val.calculated_bmi;
        if (val.occupation_23 == selectedOc) {
          s09 += val.calculated_bmi;
          s09c++;
        }
        if (val.calculated_bmi >= 30) {
          b09++;
        } else if (val.calculated_bmi >= 25) {
          o09++;
        } else {
          n09++;
        }
      } else if (obsYear == 2010) {
        v10 += val.calculated_bmi;
        if (val.occupation_23 == selectedOc) {
          s10 += val.calculated_bmi;
          s10c++;
        }
        if (val.calculated_bmi >= 30) {
          b10++;
        } else if (val.calculated_bmi >= 25) {
          o10++;
        } else {
          n10++;
        }
      } else if (obsYear == 2011) {
        v11 += val.calculated_bmi;
        if (val.occupation_23 == selectedOc) {
          s11 += val.calculated_bmi;
          s11c++;
        }
        if (val.calculated_bmi >= 30) {
          b11++;
        } else if (val.calculated_bmi >= 25) {
          o11++;
        } else {
          n11++;
        }
      } else if (obsYear == 2012) {
        v12 += val.calculated_bmi;
        if (val.occupation_23 == selectedOc) {
          s12 += val.calculated_bmi;
          s12c++;
        }
        if (val.calculated_bmi >= 30) {
          b12++;
        } else if (val.calculated_bmi >= 25) {
          o12++;
        } else {
          n12++;
        }
      } else if (obsYear == 2013) {
        v13 += val.calculated_bmi;
        if (val.occupation_23 == selectedOc) {
          s13 += val.calculated_bmi;
          s13c++;
        }
        if (val.calculated_bmi >= 30) {
          b13++;
        } else if (val.calculated_bmi >= 25) {
          o13++;
        } else {
          n13++;
        }
      } else {
        v14 += val.calculated_bmi;
        if (val.occupation_23 == selectedOc) {
          s14 += val.calculated_bmi;
          s14c++;
        }
        if (val.calculated_bmi >= 30) {
          b14++;
        } else if (val.calculated_bmi >= 25) {
          o14++;
        } else {
          n14++;
        }
      }

      // Fill in data using build functions
      buildPercents(val.occupation_8, opt7Array, val.calculated_bmi);
      buildPercents(val.occupation_8, opt8Array, val.calculated_bmi, val.gender);
      if (val.occupation_23 == "Protective service" || val.occupation_23 == "Community and social service") {
        buildPercents(val.occupation_23, opt9Array, val.calculated_bmi, val.gender);
      }
      buildBoxplot(val.occupation_8, opt10Array, val.calculated_bmi);
      // Need age group for opt11Array
      var passAge;
      if (val.calculated_age > 60) {
        passAge = "60+";
      } else if (val.calculated_age > 40) {
        passAge = "40-59";
      } else if (val.calculated_age > 20) {
        passAge = "20-39";
      } else {
        passAge = "0-19";
      }
      buildExercise(passAge, opt11Array, val.physical_quantity, val.gender);
      // Create physical level for opt12Array. FIXME - for some reason digits
      // don't work for inArray used in buildBoxPlot. So change to text.
      var physLevel;
      if (val.physical_quantity == 1) {
        physLevel = '0-149 Minutes Per Week<br />Insufficient Exercise';
      } else if (val.physical_quantity == 2) {
        physLevel = '150-299 Minutes Per Week<br />Adequate Exercise';
      } else {
        physLevel = '300+ Minutes Per Week<br />Extensive Excercise';
      }
      buildBoxplot(physLevel, opt12Array, val.calculated_bmi);
      

    }); //$.each(data, function(key, val) {

    // Sorting categories by names
    opt7Array.sort(SortByName);
    opt8Array.sort(SortByName);
    opt11Array.sort(SortByName);
    opt12Array.sort(SortByName);

    // Fill in series data for charts
    $.each(opt7Array, function(key1, val1) {
      if (val1 != "None") {
        options7.xAxis.categories.push(val1);
        var totalCount = opt7Array[val1]['n'] + opt7Array[val1]['o'] + opt7Array[val1]['b'];
        options7.series[2].data.push(opt7Array[val1]['n'] / totalCount);
        options7.series[1].data.push(opt7Array[val1]['o'] / totalCount);
        options7.series[0].data.push(opt7Array[val1]['b'] / totalCount);
      }
    });
    $.each(opt8Array, function(key1, val1) {
      if (val1 != "None") {
        options8.xAxis.categories.push(val1);
        var totalCountF = opt8Array[val1]['n'] + opt8Array[val1]['o'] + opt8Array[val1]['b'];
        var totalCountM = opt8Array[val1]['nm'] + opt8Array[val1]['om'] + opt8Array[val1]['bm'];
        options8.series[1].data.push(opt8Array[val1]['bm'] / totalCountM * 100);
        options8.series[0].data.push(opt8Array[val1]['b'] / totalCountF * 100);
      }
    });
    $.each(opt9Array, function(key1, val1) {
      options9.xAxis.categories.push(val1);
      var totalCountF = opt9Array[val1]['n'] + opt9Array[val1]['o'] + opt9Array[val1]['b'];
      var totalCountM = opt9Array[val1]['nm'] + opt9Array[val1]['om'] + opt9Array[val1]['bm'];
      options9.series[1].data.push(opt9Array[val1]['bm'] / totalCountM * 100);
      options9.series[0].data.push(opt9Array[val1]['b'] / totalCountF * 100);
    });
    $.each(opt10Array, function(key1, val1) {
      if (val1 != "None") {
        options10.xAxis.categories.push(val1);
        options10.series[0].data.push(median(opt10Array[val1]));
      }
    });
    $.each(opt11Array, function(key1, val1) {
      options11.xAxis.categories.push(val1);
      var totalCountF = opt11Array[val1]['i'] + opt11Array[val1]['a'] + opt11Array[val1]['e'];
      var totalCountM = opt11Array[val1]['im'] + opt11Array[val1]['am'] + opt11Array[val1]['em'];
      options11.series[0].data.push(opt11Array[val1]['i'] / totalCountF);
      options11.series[1].data.push(opt11Array[val1]['a'] / totalCountF);
      options11.series[2].data.push(opt11Array[val1]['e'] / totalCountF);
      options11.series[3].data.push(opt11Array[val1]['im'] / totalCountM);
      options11.series[4].data.push(opt11Array[val1]['am'] / totalCountM);
      options11.series[5].data.push(opt11Array[val1]['em'] / totalCountM);
    });
    $.each(opt12Array, function(key1, val1) {
      options12.xAxis.categories.push(val1);
      options12.series[0].data.push(median(opt12Array[val1]));
    });

    // Tally option1
    var age1 = age1n + age1o + age1b;
    var age2 = age2n + age2o + age2b;
    var age3 = age3n + age3o + age3b;
    var age4 = age4n + age4o + age4b;
    options1.series.push({
      name: "Obese",
      data: [(age1b / age1), (age2b / age2), (age3b / age3), (age4b / age4)]
    }, {
      name: "Overweight",
      data: [(age1o / age1), (age2o / age2), (age3o / age3), (age4o / age4)]
    }, {
      name: "Normal",
      data: [(age1n / age1), (age2n / age2), (age3n / age3), (age4n / age4)]
    });

    // Tally option3
    var age1f = age1nf + age1of + age1bf;
    var age2f = age2nf + age2of + age2bf;
    var age3f = age3nf + age3of + age3bf;
    var age4f = age4nf + age4of + age4bf;
    var age1m = age1nm + age1om + age1bm;
    var age2m = age2nm + age2om + age2bm;
    var age3m = age3nm + age3om + age3bm;
    var age4m = age4nm + age4om + age4bm;
    options3.series.push({
      name: "Female - Obese",
      data: [(age1bf / age1f), (age2bf / age2f), (age3bf / age3f), (age4bf / age4f)],
      stack: "Female"
    }, {
      name: "Female - Overweight",
      data: [(age1of / age1f), (age2of / age2f), (age3of / age3f), (age4of / age4f)],
      stack: "Female"
    }, {
      name: "Female - Normal",
      data: [(age1nf / age1f), (age2nf / age2f), (age3nf / age3f), (age4nf / age4f)],
      stack: "Female"
    }, {
      name: "Male - Obese",
      data: [(age1bm / age1m), (age2bm / age2m), (age3bm / age3m), (age4bm / age4m)],
      stack: "Male"
    }, {
      name: "Male - Overweight",
      data: [(age1om / age1m), (age2om / age2m), (age3om / age3m), (age4om / age4m)],
      stack: "Male"
    }, {
      name: "Male - Normal",
      data: [(age1nm / age1m), (age2nm / age2m), (age3nm / age3m), (age4nm / age4m)],
      stack: "Male"
    });

    // Tally option4
    var ocSel = ocSeln + ocSelo + ocSelb;
    var ocAll = ocAlln + ocAllo + ocAllb;
    options4.series.push({
      name: "Obese",
      data: [(ocSelb / ocSel), (ocAllb / ocAll)]
    }, {
      name: "Overweight",
      data: [(ocSelo / ocSel), (ocAllo / ocAll)]
    }, {
      name: "Normal",
      data: [(ocSeln / ocSel), (ocAlln / ocAll)]
    });
    // Tally option5
    var a08 = b08 + o08 + n08;
    var a09 = b09 + o09 + n09;
    var a10 = b10 + o10 + n10;
    var a11 = b11 + o11 + n11;
    var a12 = b12 + o12 + n12;
    var a13 = b13 + o13 + n13;
    var a14 = b14 + o14 + n14;
    options5.series.push({
      name: "Obese",
      data: [(b09 / a09), (b10 / a10), (b11 / a11), (b12 / a12), (b13 / a13), (b14 / 14)]
    }, {
      name: "Overweight",
      data: [(o09 / a09), (o10 / a10), (o11 / a11), (o12 / a12), (o13 / a13), (o14 / 14)]
    }, {
      name: "Normal",
      data: [(n09 / a09), (n10 / a10), (n11 / a11), (n12 / a12), (n13 / a13), (n14 / 14)]
    });

    options6.series.push({
      name: passedOccupation,
      data: [(s09 / s09c), (s10 / s10c), (s11 / s11c), (s12 / s12c), (s13 / s13c), (s14 / s14c)]
    }, {
      name: "All",
      data: [(v09 / a09), (v10 / a10), (v11 / a11), (v12 / a12), (v13 / a13), (v14 / a14)]
    });

    // Generate charts
    //var chart1 = new Highcharts.Chart(options1);
    //var chart2 = new Highcharts.Chart(options2);
    if (pageNumber == "obesity") {
      var chart3 = new Highcharts.Chart(options3);
      var chart5 = new Highcharts.Chart(options5);
    }
    if (pageNumber == "occupation") {
      if (hasOc) {
        var chart4 = new Highcharts.Chart(options4);
        var chart6 = new Highcharts.Chart(options6);
      }
      var chart7 = new Highcharts.Chart(options7);
      var chart8 = new Highcharts.Chart(options8);
      //var chart9 = new Highcharts.Chart(options9);
      //var chart10 = new Highcharts.Chart(options10);
    }
    if (pageNumber == "individual") {
      var chart3 = new Highcharts.Chart(options3);
    }
    if (pageNumber == "exercise") {
      var chart11 = new Highcharts.Chart(options11);
      var chart12 = new Highcharts.Chart(options12);
    }   

  });

}); // $(document).ready(function() {

// Little background parallax when scrolling
$(window).scroll(function(e){
  parallax();
});
function parallax(){
  var scrolled = $(window).scrollTop();
  $('.para-bg').css('top',-(scrolled*0.2)+'px');
}