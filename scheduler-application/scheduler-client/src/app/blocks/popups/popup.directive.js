angular
    .module('blocks.popups')
    .directive('hidePopup', function() {
        return {
            restrict: 'A',
            link: function() {

                angular.element(document.body).bind('click', function(e) {
                    var popups = document.querySelectorAll('.popover');
                    if (popups) {
                        for (var i = 0; i < popups.length; i++) {
                            var popup = popups[i];
                            var popupElement = angular.element(popup);

                            if (popupElement[0].previousSibling !== e.target) {
                                popupElement.scope().$parent.isOpen = false;
                                popupElement.remove();
                            }
                        }
                    }
                });
            }
        };
    });
