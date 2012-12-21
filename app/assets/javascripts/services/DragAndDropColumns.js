function dragOver(target, ev)
{
  ev.preventDefault();
}

function dragStart(target,ev)
{
  target.style.opacity = "0.7";
  ev.dataTransfer.setData('text/plain', "none");
  ev.dataTransfer.setData("text/html", target.innerHTML);
  ev.dataTransfer.setData("id", target.id);
  ev.dataTransfer.effectAllowed = 'move';
}

function dragEnter(target, ev) {
  ev.preventDefault();
}

function dragLeave(target, ev) {
  ev.preventDefault();
}

function dragEnd(target,ev)
{
  target.style.opacity = "1";
}

function drop(target,ev)
{
  ev.preventDefault();
  target.style.opacity = "1";

  var fromId = ev.dataTransfer.getData("id");
  fromId = fromId.split("_")[1];
  var toId = target.id;
  toId = toId.split("_")[1];

  var scope = angular.element(target).scope().$parent;
  scope.$apply(function() {
    var temp = scope.columns[fromId];
    scope.columns[fromId] = scope.columns[toId];
    scope.columns[fromId].index = toId;
    scope.columns[toId] = temp;
    scope.columns[toId].index = fromId;
    scope.$eval(scope.columns);
    var modColumnsOrder = {"cmd":"modColumnsOrder", "body":{}};
    modColumnsOrder.body.columns = [];
    for (var i = 0; i < scope.columns.length; i++) {
      modColumnsOrder.body.columns.push(scope.columns[i].title);
    };
    scope.send(modColumnsOrder);
  }); 

}