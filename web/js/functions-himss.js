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

  // Highcharts options and defaults
  var colorArray = ['#AA4643', '#4572A7', '#89A54E', '#80699B', '#3D96AE', '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92'];
  Highcharts.setOptions({
    credits: {
      enabled: false
    },
    global: {
      timezoneOffset: 8 * 60
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

  var options14 = {
    chart: {
      renderTo: 'container14',
      type: 'scatter'
    },
    colors: colorArray,
    legend: {
      labelFormatter: function() {
        var total = 0;
        for (var i = this.xData.length; i--; ) {
          total++
        }
        ;
        return 'Documents Received: ' + total;
      },
      padding: 16,
      itemStyle: {
        fontSize: "16px"
      },
      align: 'left',
      verticalAlign: 'top',
      x: 70,
      floating: true
    },
    title: {
      text: 'BMI from Healthy Weight Documents'
    },
    subtitle: {
      text: 'Collected at HIMSS &mdash; Feb. 23-26, 2014',
      useHTML: true
    },
    xAxis: {
      title: {
        text: "Collection Date and Time",
        margin: 15,
        style: {
          fontSize: "16px"
        }
      },
      dateTimeLabelFormats: {
        day: '<b>%b-%d</b>'
      },
      type: 'datetime'
    },
    yAxis: {
      title: {
        text: 'BMI',
        style: {
          fontSize: "16px"
        }
      }
    },
    tooltip: {
      formatter: function() {
        return 'Observation Date: ' + Highcharts.dateFormat('%Y-%m-%d %H:%M', this.x) + '<br />BMI: ' + this.y;
      }
    },
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
        }
      }
    },
    series: [{
        name: 'Observation',
        data: []
      }]
  };

  // Helper functions
  // Do alphabetical sort - used in chart categories
  function SortByName(a, b) {
    var aName = a.toLowerCase();
    var bName = b.toLowerCase();
    return ((aName < bName) ? -1 : ((aName > bName) ? 1 : 0));
  }

  $.getJSON('../data/showcase_data.json', function(data) {
    $.each(data, function(key, val) {
      var d = Date.parse(val.obs_date);
      options14.series[0].data.push([d, val.calculated_bmi]);
    }); //$.each(data, function(key, val) {

    // Sorting categories by names
    //opt12Array.sort(SortByName);

    // Generate charts
    var chart14 = new Highcharts.Chart(options14);

  });

}); // $(document).ready(function() {

// Little background parallax when scrolling
$(window).scroll(function(e) {
  parallax();
});
function parallax() {
  var scrolled = $(window).scrollTop();
  $('.para-bg').css('top', -(scrolled * 0.2) + 'px');
}
