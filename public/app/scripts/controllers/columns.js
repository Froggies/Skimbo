'use strict';

publicApp.controller('ColumnsCtrl', function($scope, $http) {
  if (!!window.EventSource) {
    var source = new EventSource('http://127.0.0.1:9000/api/unified2');
    source.addEventListener('message', function(e) {
    	$scope.$apply(function() {
    		if($scope.data == undefined) {
        		$scope.data = [];
        	}
    		$scope.data.unshift(JSON.parse(e.data));
    		console.log("###############")
    		//console.log($scope.originalData)
    		//$scope.data = JSON.parse(JSON.stringify($scope.originalData));
    	});
    }, false);

    source.addEventListener('open', function(e) {
      // Connection was opened.
    }, false);

    source.addEventListener('error', function(e) {
      if (e.readyState == EventSource.CLOSED) {
        // Connection was closed.
      }
    }, false);
  }
  
});


function dragOver(target, ev)
{
	ev.preventDefault();
}

function dragStart(target,ev)
{
	target.style.opacity = "0.5";
	ev.dataTransfer.setData("text/html", target.innerHTML);
	ev.dataTransfer.setData("id", target.id);
	ev.dataTransfer.effectAllowed = 'move';
}

function dragEnter(target, ev) {
	target.style.backgroundColor = "red";
	ev.preventDefault();
}

function dragLeave(target, ev) {
	target.style.backgroundColor = "pink";
	ev.preventDefault();
}

function dragEnd(target,ev)
{
	target.style.backgroundColor = "pink";
	target.style.opacity = "1";
}

function drop(target,ev)
{
	ev.preventDefault();
	var fromHtml=ev.dataTransfer.getData("text/html");
	var id=ev.dataTransfer.getData("id");

	var toHtml = target.innerHTML;

	target.innerHTML = fromHtml;
	document.getElementById(id).innerHTML = toHtml;

	target.style.backgroundColor = "pink";
	target.style.opacity = "1";
	// angular.element(target).scope().$root.$eval();
	var scope = angular.element(target).scope();
    // update the model with a wrap in $apply(fn) which will refresh the view for us
    // scope.$apply(function() {
    // 	console.log("test");
    //     scope.$eval(scope.data);
    // 	console.log("test apres");
    // }); 

}
