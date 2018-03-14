/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
$(document).ready(function () {
    $('#ProductsTableContainer').jtable({
        title: 'Table of Products',
        paging: true, //Enable paging
        pageSize: 25, //Set page size (default: 25)
        sorting: true, //Enable sorting
        defaultSorting: 'Name ASC', //Set default sorting
        actions: {
            listAction: 'api/products/list',
        },
        toolbar: {
            items: [{
                icon: 'js/jtable.2.4.0/themes/metro/add.png',
                text: 'Add new record',
                click: () => { addEditProductItem() }
            }]
        },
        fields: {
            id: {
                key: true,
                list: false
            },
            name: {
                title: 'Name',
                width: '25%'
            },
            displayName: {
                title: 'Display Name',
                width: '25%'
            },
            description: {
                title: 'Description',
                width: '50%'
            },
            edit: {
                title: '',
                width: '25',
                display: function (productData) {
                    // Store event item data in localStorage
                    var productDataItem = JSON.stringify(productData.record);
                    localStorage.setItem('productItem' + productData.record.id, productDataItem);
                    return '<img class="log4J-action-icon" src="js/jtable.2.4.0/themes/metro/edit.png" onClick="addEditProductItem(' + productData.record.id + ')" />';
                }
            },
            delete: {
                title: '',
                width: '25',
                display: function (productData) {
                    return '<img class="log4J-action-icon" src="js/jtable.2.4.0/themes/metro/delete.png" onClick="deleteProductItem(' + productData.record.id + ')" />';
                }
            }
        }
    });
    $.ajax({
        type: 'POST',
        url: 'api/events/list',
        success:function(response){
            if (response.Result === 'OK') {
                var allEvents = response.Records.map((item) => {
                    return item.name;
                });
                localStorage.setItem('allEvents', allEvents);
            }
        },
        error:function(jqXhr, textStatus, errorThrown){
            console.error(textStatus + ' - ' + errorThrown);
        }
    });
    //Load products list from server
    $('#ProductsTableContainer').jtable('load');
});

function deleteProductItem(productId) {
    var response = confirm('Are you sure you want to delete this product?');
    if (response) {
      var postData = {};
      postData['id'] = productId;
      $.ajax({
          type: 'POST',
          contentType: 'application/json',
          url: 'api/products/delete',
          data: JSON.stringify(postData),
          success:function(response) {
              if (response.Result === 'OK') {
                  $('#ProductsTableContainer').jtable('load');
              }
          },
          error:function(jqXhr, textStatus, errorThrown) {
              console.error(textStatus + ' - ' + errorThrown);
          }
      });
    }
}

function addEditProductItemHandler() {
    var validForm = validateFormContent();
    if (validForm) {
        showLoadingAnimation();
        var postUrl = 'api/products/create';
        var postData = {};
        var productEvents = [];
        postData['name'] = $('#productName').val();
        postData['displayName'] = $('#productDisplayName').val();
        postData['description'] = $('#productDescription').val();
        $('#productEvents .product-event-row').each(function() {
            productEvents.push($(this).find('input')[0].value);
        });
        postData['events'] = productEvents;
        if ($('#productId').val()) {
            postUrl = 'api/products/update';
            postData['id'] = $('#productId').val();
        }
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            url: postUrl,
            data: JSON.stringify(postData),
            success:function(response) {
                if (response.Result === 'OK') {
                    $('#ProductsTableContainer').jtable('load');
                    closeLog4jModal();
                }
            },
            error:function(jqXhr, textStatus, errorThrown) {
                console.error(textStatus + ' - ' + errorThrown);
            }
        });
    }
}

function addEditProductItem(productId) {
    var hiddenIdField = '';
    var productData = {};
    if (productId) {
        hiddenIdField = '<input type="hidden" id="productId" name="id" value="' + productId + '" />';
    } else {
        productId = 'tempProductData';
        var tempProductData = {
            id: productId,
            events: [],
        }
        localStorage.setItem('productItem' + productId, JSON.stringify(tempProductData));
    }
    productFormContent = ' \
        <form id="add-edit-product-form" class="log4j-catalog-form" method="post"> \
          ' + hiddenIdField + ' \
          <p> \
              <label>Name</label> \
              <input type="text" id="productName" name="name" class="required" /> \
          </p> \
          <p> \
              <label>Display Name</label> \
              <input type="text" id="productDisplayName" name="displayName" class="required" /> \
          </p> \
          <p> \
              <label>Description</label> \
              <input type="text" id="productDescription" name="description" class="required" /> \
          </p> \
          <p> \
              <label>Assigned Events</label> \
              <span id="productEvents"></span> \
          </p> \
          <p> \
              <label>Add Event</label> \
              <span> \
                  <select name="addProductEvent" id="addProductEvent"> \
                      <option value="">loading...</option> \
                  </select> \
                  <button id="addProductEventButton">+</button> \
              </span> \
          </p> \
        </form> \
        <div class="log4j-catalog-button-row"> \
            <button class="log4j-catalog-button" onclick="closeLog4jModal()">Cancel</button>\
            <button class="log4j-catalog-button" onclick="addEditProductItemHandler()">Save</button> \
        </div> \
    ';
    showLog4JModal('Add / Edit Product Item', productFormContent);
    if (localStorage.getItem('productItem' + productId)) {
        productData = JSON.parse(localStorage.getItem('productItem' + productId));
        $('#productName').val(productData.name);
        $('#productDisplayName').val(productData.displayName);
        $('#productDescription').val(productData.description);
    }
    populateProductEvents(productData.events, productId);
}

function populateProductEvents(assignedEvents, productId) {
    var selectedEvents = [];
    $('#productEvents').children().remove();
    if (productId) {
        assignedEvents.map((item) => {
            selectedEvents.push(item);
            $('#productEvents').append(' \
                <span class="product-event-row"> \
                    <input type="text" name="events[]" value="' + item + '" disabled /> \
                    <button class="remove-product-event-button" alt="' + productId + '" rel="' + item + '">-</button> \
                </span> \
            ');
        });
    }
    function checkPendingRequest() {
        if ($.active > 0) {
            window.setTimeout(checkPendingRequest, 1000);
        } else {
            var allEvents = localStorage.getItem('allEvents').split(',');
            allEvents.sort();
            $('#addProductEvent option').remove();
            allEvents.map((item) => {
                if (!selectedEvents.includes(item)) {
                    $('#addProductEvent').append(' \
                        <option value="' + item + '">' + item + '</option> \
                    ');
                }
            });
        }
    };
    checkPendingRequest();
    assignProductEventListeners(productId);
}

function assignProductEventListeners(productId) {
    $('#addProductEventButton, .remove-product-event-button').unbind();
    $('#addProductEventButton').click(function(e) {
        e.preventDefault();
        var allEvents = localStorage.getItem('allEvents').split(',');
        var productData = JSON.parse(localStorage.getItem('productItem' + productId));
        productData.events.push($('#addProductEvent').val());
        localStorage.setItem('productItem' + productId, JSON.stringify(productData));
        populateProductEvents(productData.events, productId);
    });

    $('.remove-product-event-button').click(function(e) {
        e.preventDefault();
        var allEvents = localStorage.getItem('allEvents').split(',');
        var productData = JSON.parse(localStorage.getItem('productItem' + productId));
        productData.events.pop($(this).attr('rel'));
        localStorage.setItem('productItem' + productId, JSON.stringify(productData));
        populateProductEvents(productData.events, productId);
    });
}
