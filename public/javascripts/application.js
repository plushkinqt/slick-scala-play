function loadCat(id) {
  $( "#content").load("/views/cat/" + id, function() {
    $.ajax({
      url: "/cat/" + id
    }).done(function (data) {
      $("#one-cat-table").attr("data-id", data.id);
      $("#name .value").text(data.name);
      $("#color .value").text(data.color);
      $("#race .value").text(data.race);
      $("#gender .value").text(data.gender);
      $("#thumb").attr("src", data.url);
    })
  });
}

function loadAllCats() {
  $( "#content" ).load( "/views/cats", function() {
    $.ajax({
      url: "/cats"
    }).done(function( data ) {
      for(var i = 0; i < data.length; i++){
        $("#cats-list-table").append("<tr data-id=" + data[i].id + "><td><img class='thumb' src='" + data[i].url + "'></td><td>" + data[i].name + "</td><td>" + data[i].color + "</td><td>" + data[i].race + "</td><td>" + data[i].gender + "</td></tr>");
      }
      $("#cats-list-table tr").click(function() {
        loadCat($(this).attr("data-id"));
      });
      $("#cat-form-page").click(function(){
        $.ajax({
          url: "/form"
        }).done(function( data ) {

          $("#all-cats-page").removeClass("active");
          $("#cat-form-page").addClass("active");
          $("#content").load("/form");
        })
      });
    })
  });
}

function loadEditCat(id) {
  $( "#content" ).load( "/edit", function() {
    $.ajax({
      url: "/cat/" + id
    }).done(function (data) {
      $("#hidden-id").val(data.id);
      $("#inputName").val(data.name);
      $("#inputColor").val(data.color);
      $("#inputRace").val(data.race);
      $("#inputGender").val(data.gender);
      $("#inputUrl").val(data.url);
    })
  })
}

