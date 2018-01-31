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
    $('#EventsTableContainer').jtable({
        title: 'Table of events',
        paging: true, //Enable paging
        pageSize: 25, //Set page size (default: 25)
        sorting: true, //Enable sorting
        defaultSorting: 'Name ASC', //Set default sorting
        actions: {
            listAction: 'api/events/list',
        },
        toolbar: {
            items: [{
                icon: 'js/jtable.2.4.0/themes/metro/add.png',
                text: 'Add new record',
                click: () => { addEditEventItem() }
            }]
        },
        fields: {
            id: {
                key: true,
                list: false
            },
            name: {
                title: 'Name',
                width: '15%'
            },
            displayName: {
                title: 'Display Name',
                width: '15%'
            },
            description: {
                title: 'Description',
                width: '30%'
            },
            attributes: {
                title: 'Assigned Attributes',
                width: 'auto',
                sorting: false,
                edit: false,
                create: false,
                display: function (eventData) {
                    var attributeList = eventData.record.attributes.map(function(elem){return elem.name + (elem.required ? ' (required)' : '')}).join(' | ');
                    //Create a div that will be used to view associated attributes
                    var $divAttributes = $('<div class="event-attributes">' + attributeList + '</div>');
                    return $divAttributes;
                }
            },
            edit: {
                title: '',
                width: '25',
                display: function (eventData) {
                    // Store event item data in localStorage
                    var eventDataItem = JSON.stringify(eventData.record);
                    localStorage.setItem('eventItem' + eventData.record.id, eventDataItem);
                    return '<img class="log4J-action-icon" src="js/jtable.2.4.0/themes/metro/edit.png" onClick="addEditEventItem(' + eventData.record.id + ')" />';
                }
            },
            delete: {
                title: '',
                width: '25',
                display: function (eventData) {
                    return '<img class="log4J-action-icon" src="js/jtable.2.4.0/themes/metro/delete.png" onClick="deleteEventItem(' + eventData.record.id + ')" />';
                }
            }
        }
    });
    $.ajax({
        type: 'GET',
        url: 'api/attributes',
        success:function(response){
            if (response.result === 'OK') {
                localStorage.setItem('allAttributes', response.data);
            }
        },
        error:function(jqXhr, textStatus, errorThrown){
            console.error(textStatus + ' - ' + errorThrown);
        }
    });
    $('#EventsTableContainer').jtable('load');
});

function deleteEventItem(eventId) {
    var response = confirm('Are you sure you want to delete this event?');
    if (response) {
      var postData = {};
      postData['id'] = eventId;
      $.ajax({
          type: 'POST',
          contentType: 'application/json',
          url: 'api/events/delete',
          data: JSON.stringify(postData),
          success:function(response) {
              if (response.Result === 'OK') {
                  $('#EventsTableContainer').jtable('load');
              }
          },
          error:function(jqXhr, textStatus, errorThrown) {
              console.error(textStatus + ' - ' + errorThrown);
          }
      });
    }
}

function addEditEventItemHandler() {
    var validForm = validateFormContent();
    if (validForm) {
        showLoadingAnimation();
        var postUrl = 'api/events/create';
        var postData = {};
        var eventAttributes = [];
        postData['name'] = $('#eventName').val();
        postData['displayName'] = $('#eventDisplayName').val();
        postData['description'] = $('#eventDescription').val();
        $('#eventAttributes .event-attribute-row').each(function() {
            var eventAttributeItem = {
                name: $(this).find('input')[0].value,
                required: $(this).find('input')[1].checked,
            };
            eventAttributes.push(eventAttributeItem);
        });
        postData['attributes'] = eventAttributes;
        if ($('#eventId').val()) {
            postUrl = 'api/events/update';
            postData['id'] = $('#eventId').val();
        }
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            url: postUrl,
            data: JSON.stringify(postData),
            success:function(response) {
                if (response.Result === 'OK') {
                    $('#EventsTableContainer').jtable('load');
                    closeLog4jModal();
                }
            },
            error:function(jqXhr, textStatus, errorThrown) {
                console.error(textStatus + ' - ' + errorThrown);
            }
        });
    }
}

function addEditEventItem(eventId) {
    var hiddenIdField = '';
    var eventData = {};
    if (eventId) {
        hiddenIdField = '<input type="hidden" id="eventId" name="id" value="' + eventId + '" />';
    } else {
        eventId = 'tempEventData';
        var tempEventData = {
            id: eventId,
            attributes: [],
        }
        localStorage.setItem('eventItem' + eventId, JSON.stringify(tempEventData));
    }
    eventFormContent = ' \
        <form id="add-edit-event-form" class="log4j-catalog-form" method="post"> \
          ' + hiddenIdField + ' \
          <p> \
              <label>Name</label> \
              <input type="text" id="eventName" name="name" class="required" /> \
          </p> \
          <p> \
              <label>Display Name</label> \
              <input type="text" id="eventDisplayName" name="displayName" class="required" /> \
          </p> \
          <p> \
              <label>Description</label> \
              <input type="text" id="eventDescription" name="description" class="required" /> \
          </p> \
          <p> \
              <label>Assigned Attributes</label> \
              <span id="eventAttributes"></span> \
          </p> \
          <p> \
              <label>Add Attribute</label> \
              <span> \
                  <select name="addEventAttribute" id="addEventAttribute"> \
                      <option value="">--</option> \
                  </select> \
                  <button id="addEventAttributeButton">+</button> \
              </span> \
          </p> \
        </form> \
        <div class="log4j-catalog-button-row"> \
            <button class="log4j-catalog-button" onclick="closeLog4jModal()">Cancel</button>\
            <button class="log4j-catalog-button" onclick="addEditEventItemHandler()">Save</button> \
        </div> \
    ';
    showLog4JModal('Add / Edit Event Item', eventFormContent);
    if (localStorage.getItem('eventItem' + eventId)) {
        eventData = JSON.parse(localStorage.getItem('eventItem' + eventId));
        $('#eventName').val(eventData.name);
        $('#eventDisplayName').val(eventData.displayName);
        $('#eventDescription').val(eventData.description);
    }
    populateEventAttributes(eventData.attributes, eventId);
}

function populateEventAttributes(assignedAttributes, eventId) {
    var selectedAttributes = [];
    var allAttributes = localStorage.getItem('allAttributes').split(',');
    $('#eventAttributes').children().remove();
    if (eventId) {
        assignedAttributes.map((item) => {
            selectedAttributes.push(item.name);
            var attributeRequired = item.required ? 'checked' : '';
            $('#eventAttributes').append(' \
                <span class="event-attribute-row"> \
                    <input type="text" name="attributes[]" value="' + item.name + '" disabled /> \
                    <input type="checkbox" ' + attributeRequired + ' /> \
                    <span class="event-attribute-item-required">required</span> \
                    <button class="remove-event-attribute-button" alt="' + eventId + '" rel="' + item.name + '">-</button> \
                </span> \
            ');
        });
    }
    allAttributes.sort();
    $('#addEventAttribute option').remove();
    allAttributes.map((item) => {
        if (!selectedAttributes.includes(item)) {
            $('#addEventAttribute').append(' \
                <option value="' + item + '">' + item + '</option> \
            ');
        }
    });
    assignEventAttributeListeners(eventId);
}

function assignEventAttributeListeners(eventId) {
    $('#addEventAttributeButton, .remove-event-attribute-button').unbind();
    $('#addEventAttributeButton').click(function(e) {
        e.preventDefault();
        var allAttributes = localStorage.getItem('allAttributes').split(',');
        var eventData = JSON.parse(localStorage.getItem('eventItem' + eventId));
        eventData.attributes.push({name: $('#addEventAttribute').val(), required: false});
        localStorage.setItem('eventItem' + eventId, JSON.stringify(eventData));
        populateEventAttributes(eventData.attributes, eventId);
    });

    $('.remove-event-attribute-button').click(function(e) {
        e.preventDefault();
        var allAttributes = localStorage.getItem('allAttributes').split(',');
        var eventData = JSON.parse(localStorage.getItem('eventItem' + eventId));
        var newAttributes = eventData.attributes.filter((obj) => {
            return obj.name !== $(this).attr('rel');
        });
        eventData['attributes'] = newAttributes;
        localStorage.setItem('eventItem' + eventId, JSON.stringify(eventData));
        populateEventAttributes(eventData.attributes, eventId);
    });
}
