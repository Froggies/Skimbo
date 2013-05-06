'use strict';

describe('Controller: ColumnsCtrl', function() {

  // load the controller's module
  beforeEach(module('publicApp'));

  var ColumnsCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function($controller) {
    scope = {};
    ColumnsCtrl = $controller('ColumnsCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function() {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
