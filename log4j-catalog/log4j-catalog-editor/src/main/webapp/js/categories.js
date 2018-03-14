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
    $('#CategoriesTableContainer').jtable({
        title: 'Table of categories',
        paging: true, //Enable paging
        pageSize: 25, //Set page size (default: 25)
        sorting: true, //Enable sorting
        defaultSorting: 'Name ASC', //Set default sorting
        actions: {
            listAction: 'api/categories/list',
        },
        toolbar: {
            items: [{
                icon: 'js/jtable.2.4.0/themes/metro/add.png',
                text: 'Add new record',
                click: () => { addEditCategoryItem() }
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
                display: function (categoryData) {
                    // Store event item data in localStorage
                    var categoryDataItem = JSON.stringify(categoryData.record);
                    localStorage.setItem('categoryItem' + categoryData.record.id, categoryDataItem);
                    return '<img class="log4J-action-icon" src="js/jtable.2.4.0/themes/metro/edit.png" onClick="addEditCategoryItem(' + categoryData.record.id + ')" />';
                }
            },
            delete: {
                title: '',
                width: '25',
                display: function (categoryData) {
                    return '<img class="log4J-action-icon" src="js/jtable.2.4.0/themes/metro/delete.png" onClick="deleteCategoryItem(' + categoryData.record.id + ')" />';
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
    //Load categories list from server
    $('#CategoriesTableContainer').jtable('load');
});

function deleteCategoryItem(categoryId) {
    var response = confirm('Are you sure you want to delete this category?');
    if (response) {
      var postData = {};
      postData['id'] = categoryId;
      $.ajax({
          type: 'POST',
          contentType: 'application/json',
          url: 'api/categories/delete',
          data: JSON.stringify(postData),
          success:function(response) {
              if (response.Result === 'OK') {
                  $('#CategoriesTableContainer').jtable('load');
              }
          },
          error:function(jqXhr, textStatus, errorThrown) {
              console.error(textStatus + ' - ' + errorThrown);
          }
      });
    }
}

function addEditCategoryItemHandler() {
    var validForm = validateFormContent();
    if (validForm) {
        showLoadingAnimation();
        var postUrl = 'api/categories/create';
        var postData = {};
        var categoryEvents = [];
        postData['name'] = $('#categoryName').val();
        postData['displayName'] = $('#categoryDisplayName').val();
        postData['description'] = $('#categoryDescription').val();
        $('#categoryEvents .category-event-row').each(function() {
            categoryEvents.push($(this).find('input')[0].value);
        });
        postData['events'] = categoryEvents;
        if ($('#categoryId').val()) {
            postUrl = 'api/categories/update';
            postData['id'] = $('#categoryId').val();
        }
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            url: postUrl,
            data: JSON.stringify(postData),
            success:function(response) {
                if (response.Result === 'OK') {
                    $('#CategoriesTableContainer').jtable('load');
                    closeLog4jModal();
                }
            },
            error:function(jqXhr, textStatus, errorThrown) {
                console.error(textStatus + ' - ' + errorThrown);
            }
        });
    }
}

function addEditCategoryItem(categoryId) {
    var hiddenIdField = '';
    var categoryData = {};
    if (categoryId) {
        hiddenIdField = '<input type="hidden" id="categoryId" name="id" value="' + categoryId + '" />';
    } else {
        categoryId = 'tempCategoryData';
        var tempCategoryData = {
            id: categoryId,
            events: [],
        }
        localStorage.setItem('categoryItem' + categoryId, JSON.stringify(tempCategoryData));
    }
    categoryFormContent = ' \
        <form id="add-edit-category-form" class="log4j-catalog-form" method="post"> \
          ' + hiddenIdField + ' \
          <p> \
              <label>Name</label> \
              <input type="text" id="categoryName" name="name" class="required" /> \
          </p> \
          <p> \
              <label>Display Name</label> \
              <input type="text" id="categoryDisplayName" name="displayName" class="required" /> \
          </p> \
          <p> \
              <label>Description</label> \
              <input type="text" id="categoryDescription" name="description" class="required" /> \
          </p> \
          <p> \
              <label>Assigned Events</label> \
              <span id="categoryEvents"></span> \
          </p> \
          <p> \
              <label>Add Event</label> \
              <span> \
                  <select name="addCategoryEvent" id="addCategoryEvent"> \
                      <option value="">loading...</option> \
                  </select> \
                  <button id="addCategoryEventButton">+</button> \
              </span> \
          </p> \
        </form> \
        <div class="log4j-catalog-button-row"> \
            <button class="log4j-catalog-button" onclick="closeLog4jModal()">Cancel</button>\
            <button class="log4j-catalog-button" onclick="addEditCategoryItemHandler()">Save</button> \
        </div> \
    ';
    showLog4JModal('Add / Edit Category Item', categoryFormContent);
    if (localStorage.getItem('categoryItem' + categoryId)) {
        categoryData = JSON.parse(localStorage.getItem('categoryItem' + categoryId));
        $('#categoryName').val(categoryData.name);
        $('#categoryDisplayName').val(categoryData.displayName);
        $('#categoryDescription').val(categoryData.description);
    }
    populateCategoryEvents(categoryData.events, categoryId);
}

function populateCategoryEvents(assignedEvents, categoryId) {
    var selectedEvents = [];
    $('#categoryEvents').children().remove();
    if (categoryId) {
        assignedEvents.map((item) => {
            selectedEvents.push(item);
            $('#categoryEvents').append(' \
                <span class="category-event-row"> \
                    <input type="text" name="events[]" value="' + item + '" disabled /> \
                    <button class="remove-category-event-button" alt="' + categoryId + '" rel="' + item + '">-</button> \
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
            $('#addCategoryEvent option').remove();
            allEvents.map((item) => {
                if (!selectedEvents.includes(item)) {
                    $('#addCategoryEvent').append(' \
                        <option value="' + item + '">' + item + '</option> \
                    ');
                }
            });
        }
    };
    checkPendingRequest();
    assignCategoryEventListeners(categoryId);
}

function assignCategoryEventListeners(categoryId) {
    $('#addCategoryEventButton, .remove-category-event-button').unbind();
    $('#addCategoryEventButton').click(function(e) {
        e.preventDefault();
        var allEvents = localStorage.getItem('allEvents').split(',');
        var categoryData = JSON.parse(localStorage.getItem('categoryItem' + categoryId));
        categoryData.events.push($('#addCategoryEvent').val());
        localStorage.setItem('categoryItem' + categoryId, JSON.stringify(categoryData));
        populateCategoryEvents(categoryData.events, categoryId);
    });

    $('.remove-category-event-button').click(function(e) {
        e.preventDefault();
        var allEvents = localStorage.getItem('allEvents').split(',');
        var categoryData = JSON.parse(localStorage.getItem('categoryItem' + categoryId));
        categoryData.events.pop($(this).attr('rel'));
        localStorage.setItem('categoryItem' + categoryId, JSON.stringify(categoryData));
        populateCategoryEvents(categoryData.events, categoryId);
    });
}
