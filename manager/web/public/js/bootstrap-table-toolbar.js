/**
 * @author: aperez <aperez@datadec.es>
 * @version: v2.0.0
 *
 * @update Dennis Hernández <http://djhvscf.github.io/Blog>
 */

!function($) {
    'use strict';

    var firstLoad = false;

    var sprintf = $.fn.bootstrapTable.utils.sprintf;
    
    var calculateObjectValue = function (self, name, args, defaultValue) {
        var func = name;

        if (typeof name === 'string') {
            // support obj.func1.func2
            var names = name.split('.');

            if (names.length > 1) {
                func = window;
                $.each(names, function (i, f) {
                    func = func[f];
                });
            } else {
                func = window[name];
            }
        }
        if (typeof func === 'object') {
            return func;
        }
        if (typeof func === 'function') {
            return func.apply(self, args);
        }
        if (!func && typeof name === 'string' && sprintf.apply(this, [name].concat(args))) {
            return sprintf.apply(this, [name].concat(args));
        }
        return defaultValue;
    };
    
    var getItemField = function (item, field, escape) {
        var value = item;

        if (typeof field !== 'string' || item.hasOwnProperty(field)) {
            return escape ? escapeHTML(item[field]) : item[field];
        }
        var props = field.split('.');
        for (var p in props) {
            value = value && value[props[p]];
        }
        return escape ? escapeHTML(value) : value;
    };
    
    var getFieldIndex = function (columns, field) {
        var index = -1;

        $.each(columns, function (i, column) {
            if (column.field === field) {
                index = i;
                return false;
            }
            return true;
        });
        return index;
    };

    var showAvdSearch = function(pColumns, searchTitle, searchText, that) {
        if (!$("#avdSearchModal" + "_" + that.options.idTable).hasClass("modal")) {
            var vModal = sprintf("<div id=\"avdSearchModal%s\"  class=\"modal fade\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"mySmallModalLabel\" aria-hidden=\"true\">", "_" + that.options.idTable);
            vModal += "<div class=\"modal-dialog modal-xs\">";
            vModal += " <div class=\"modal-content\">";
            vModal += "  <div class=\"modal-header\">";
            vModal += "   <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\" >&times;</button>";
            vModal += sprintf("   <h4 class=\"modal-title\">%s</h4>", searchTitle);
            vModal += "  </div>";
            vModal += "  <div class=\"modal-body modal-body-custom\">";
            vModal += sprintf("   <div class=\"container-fluid\" id=\"avdSearchModalContent%s\" style=\"padding-right: 0px;padding-left: 0px;\" >", "_" + that.options.idTable);
            vModal += "   </div>";
            vModal += "  </div>";
            vModal += "  </div>";
            vModal += " </div>";
            vModal += "</div>";

            $("body").append($(vModal));

            var vFormAvd = createFormAvd(pColumns, searchText, that),
                timeoutId = 0;;

            $('#avdSearchModalContent' + "_" + that.options.idTable).append(vFormAvd.join(''));

            $("#btnCloseAvd" + "_" + that.options.idTable).click(function(event) {
            	that.onColumnAdvancedSearch(event);
                $("#avdSearchModal" + "_" + that.options.idTable).modal('hide');
            });

            $("#avdSearchModal" + "_" + that.options.idTable).modal();
        } else {
            $("#avdSearchModal" + "_" + that.options.idTable).modal();
        }
    };

    var createFormAvd = function(pColumns, searchText, that) {
        var htmlForm = [];
        htmlForm.push(sprintf('<form class="form-horizontal" id="%s" action="%s" >', that.options.idForm, that.options.actionForm));
        for (var i in pColumns) {
            var vObjCol = pColumns[i];
            if (!vObjCol.checkbox && vObjCol.visible && vObjCol.searchable) {
                htmlForm.push('<div class="form-group">');
                htmlForm.push(sprintf('<label class="col-sm-4 control-label">%s</label>', vObjCol.title));
                htmlForm.push('<div class="col-sm-6">');
                htmlForm.push(sprintf('<input type="text" class="form-control input-md" name="%s" placeholder="%s" id="%s">', vObjCol.field, vObjCol.title, vObjCol.field));
                htmlForm.push('</div>');
                htmlForm.push('</div>');
            }
        }

        htmlForm.push('<div class="form-group">');
        htmlForm.push('<div class="col-sm-offset-9 col-sm-3">');
        htmlForm.push(sprintf('<button type="button" id="btnCloseAvd%s" class="btn btn-primary" >%s</button>', "_" + that.options.idTable, searchText));
        htmlForm.push('</div>');
        htmlForm.push('</div>');
        htmlForm.push('</form>');

        return htmlForm;
    };

    $.extend($.fn.bootstrapTable.defaults, {
        advancedSearch: false,
        idForm: 'advancedSearch',
        actionForm: '',
        idTable: undefined,
        onColumnAdvancedSearch: function (field, text) {
            return false;
        }
    });

    $.extend($.fn.bootstrapTable.defaults.icons, {
        advancedSearchIcon: 'glyphicon glyphicon-search'
    });

    $.extend($.fn.bootstrapTable.Constructor.EVENTS, {
        'column-advanced-search.bs.table': 'onColumnAdvancedSearch'
    });

    $.extend($.fn.bootstrapTable.locales, {
        formatAdvancedSearch: function() {
            return '高级搜索';
        },
        formatAdvancedCloseButton: function() {
            return "确定";
        }
    });

    $.extend($.fn.bootstrapTable.defaults, $.fn.bootstrapTable.locales);

    var BootstrapTable = $.fn.bootstrapTable.Constructor,
        _initToolbar = BootstrapTable.prototype.initToolbar,        
        _load = BootstrapTable.prototype.load,
        _initSearch = BootstrapTable.prototype.initSearch;

    BootstrapTable.prototype.initToolbar = function() {
        _initToolbar.apply(this, Array.prototype.slice.apply(arguments));

        if (!this.options.search) {
            return;
        }

        if (!this.options.advancedSearch) {
            return;
        }

        if (!this.options.idTable) {
            return;
        }

        var that = this,
            html = [];

        html.push(sprintf('<div class="columns columns-%s btn-group pull-%s" role="group">', this.options.buttonsAlign, this.options.buttonsAlign));
        html.push(sprintf('<button class="btn btn-default%s' + '" type="button" name="advancedSearch" title="%s">', that.options.iconSize === undefined ? '' : ' btn-' + that.options.iconSize, that.options.formatAdvancedSearch()));
        html.push(sprintf('<i class="%s %s"></i>', that.options.iconsPrefix, that.options.icons.advancedSearchIcon))
        html.push('</button></div>');

        that.$toolbar.prepend(html.join(''));

        that.$toolbar.find('button[name="advancedSearch"]')
            .off('click').on('click', function() {
                showAvdSearch(that.columns, that.options.formatAdvancedSearch(), that.options.formatAdvancedCloseButton(), that);
            });
    };

    BootstrapTable.prototype.load = function(data) {
        _load.apply(this, Array.prototype.slice.apply(arguments));

        if (!this.options.advancedSearch) {
            return;
        }

        if (typeof this.options.idTable === 'undefined') {
            return;
        } else {
            if (!firstLoad) {
                var height = parseInt($(".bootstrap-table").height());
                height += 10;
                $("#" + this.options.idTable).bootstrapTable("resetView", {height: height});
                firstLoad = true;
            }
        }
    };

    BootstrapTable.prototype.initSearch = function () {
        _initSearch.apply(this, Array.prototype.slice.apply(arguments));

        if (!this.options.advancedSearch) {
            return;
        }

        var that = this;
        var fp = $.isEmptyObject(this.filterColumnsPartial) ? null : this.filterColumnsPartial;

        this.data = fp ? $.grep(this.data, function (item, i) {
            for (var key in fp) {
                var fval = fp[key].toLowerCase();
                var value = item[key];
                value = $.fn.bootstrapTable.utils.calculateObjectValue(that.header,
                    that.header.formatters[$.inArray(key, that.header.fields)],
                    [value, item, i], value);

                if (!($.inArray(key, that.header.fields) !== -1 &&
                    (typeof value === 'string' || typeof value === 'number') &&
                    (value + '').toLowerCase().indexOf(fval) !== -1)) {
                    return false;
                }
            }
            return true;
        }) : this.data;
    };

    BootstrapTable.prototype.onColumnAdvancedSearch = function (event) {
    	
    	var $this = this;
        $this.filterColumnsPartial = {};
        $('#advancedSearch').find('input[name]').each(function () {
        	var val = $(this).val();
        	if(val != null && val.length > 0){
        		var text = $(this).attr('name');
//        		if ($.isEmptyObject($this.filterColumnsPartial)) {
//                    $this.filterColumnsPartial = {};
//                }
                if (val) {
                    $this.filterColumnsPartial[text] = val;
                } else {
                    delete $this.filterColumnsPartial[text];
                }
        		
        		$this.options.pageNumber = 1;
//                $this.onSearch(event);
//                $this.updatePagination();
        	}
        });
        
        
        var params = {
            searchText: this.searchText,
            sortName: this.options.sortName,
            sortOrder: this.options.sortOrder
        },data={};
        if(this.options.pagination) {
            params.pageSize = this.options.pageSize === this.options.formatAllRows() ?
                this.options.totalRows : this.options.pageSize;
            params.pageNumber = this.options.pageNumber;
        }
        if (!this.options.url && !this.options.ajax) {
            return;
        }

        if (this.options.queryParamsType === 'limit') {
            params = {
                search: params.searchText,
                sort: params.sortName,
                order: params.sortOrder
            };
            if (this.options.pagination) {
                params.limit = this.options.pageSize === this.options.formatAllRows() ?
                    this.options.totalRows : this.options.pageSize;
                params.offset = this.options.pageSize === this.options.formatAllRows() ?
                    0 : this.options.pageSize * (this.options.pageNumber - 1);
            }
        }
        
        if (!($.isEmptyObject(this.filterColumnsPartial))) {
            params['filter'] = JSON.stringify(this.filterColumnsPartial, null);
        }
        
        data = params;
        
        $.ajax({
            type: this.options.method,
            url: this.options.url,
            data: this.options.contentType === 'application/json' && this.options.method === 'post' ?
                JSON.stringify(data) : data,
            cache: this.options.cache,
            contentType: this.options.contentType,
            dataType: this.options.dataType,
            success: function (res) {
//            	$this.load(res);
            	var fixedScroll = false;

                // #431: support pagination
                if ($this.options.sidePagination === 'server') {
                	$this.options.totalRows = res.total;
                    fixedScroll = res.fixedScroll;
                    data = res[$this.options.dataField];
                } else if (!$.isArray(data)) { // support fixedScroll
                    fixedScroll = data.fixedScroll;
                    data = data.data;
                }

                $this.initData(data);
                $this.initSearch();
                $this.initPagination();
                $this.reBody(fixedScroll,data);
            },
            error: function (res) {
            	$this.trigger('load-error', res.status, res);
                if (!silent) $this.$tableLoading.hide();
            }
        });
    	
    };
    
    BootstrapTable.prototype.reBody = function (fixedScroll,data) {
    	var that = this,
        html = [];
//        data = this.getData();

	    this.trigger('pre-body', data);
	
	    this.$body = this.$el.find('>tbody');
	    if (!this.$body.length) {
	        this.$body = $('<tbody></tbody>').appendTo(this.$el);
	    }
	
	    //Fix #389 Bootstrap-table-flatJSON is not working
	
	    if (!this.options.pagination || this.options.sidePagination === 'server') {
	        this.pageFrom = 1;
	        this.pageTo = data.length;
	    }
	
	    for (var i = this.pageFrom - 1; i < this.pageTo; i++) {
	        var key,
	            item = data[i],
	            style = {},
	            csses = [],
	            data_ = '',
	            attributes = {},
	            htmlAttributes = [];
	
	        style = calculateObjectValue(this.options, this.options.rowStyle, [item, i], style);
	
	        if (style && style.css) {
	            for (key in style.css) {
	                csses.push(key + ': ' + style.css[key]);
	            }
	        }
	
	        attributes = calculateObjectValue(this.options,
	            this.options.rowAttributes, [item, i], attributes);
	
	        if (attributes) {
	            for (key in attributes) {
	                htmlAttributes.push(sprintf('%s="%s"', key, escapeHTML(attributes[key])));
	            }
	        }
	
	        if (item._data && !$.isEmptyObject(item._data)) {
	            $.each(item._data, function (k, v) {
	                // ignore data-index
	                if (k === 'index') {
	                    return;
	                }
	                data_ += sprintf(' data-%s="%s"', k, v);
	            });
	        }
	
	        html.push('<tr',
	            sprintf(' %s', htmlAttributes.join(' ')),
	            sprintf(' id="%s"', $.isArray(item) ? undefined : item._id),
	            sprintf(' class="%s"', style.classes || ($.isArray(item) ? undefined : item._class)),
	            sprintf(' data-index="%s"', i),
	            sprintf(' data-uniqueid="%s"', item[this.options.uniqueId]),
	            sprintf('%s', data_),
	            '>'
	        );
	
	        if (this.options.cardView) {
	            html.push(sprintf('<td colspan="%s">', this.header.fields.length));
	        }
	
	        if (!this.options.cardView && this.options.detailView) {
	            html.push('<td>',
	                '<a class="detail-icon" href="javascript:">',
	                sprintf('<i class="%s %s"></i>', this.options.iconsPrefix, this.options.icons.detailOpen),
	                '</a>',
	                '</td>');
	        }
	
	        $.each(this.header.fields, function (j, field) {
	            var text = '',
	                value = getItemField(item, field, that.options.escape),
	                type = '',
	                cellStyle = {},
	                id_ = '',
	                class_ = that.header.classes[j],
	                data_ = '',
	                rowspan_ = '',
	                colspan_ = '',
	                title_ = '',
	                column = that.columns[j];
	
	            if (that.fromHtml && typeof value === 'undefined') {
	                return;
	            }
	
	            if (!column.visible) {
	                return;
	            }
	
	            if (that.options.cardView && (!column.cardVisible)) {
	                return;
	            }
	
	            style = sprintf('style="%s"', csses.concat(that.header.styles[j]).join('; '));
	
	            // handle td's id and class
	            if (item['_' + field + '_id']) {
	                id_ = sprintf(' id="%s"', item['_' + field + '_id']);
	            }
	            if (item['_' + field + '_class']) {
	                class_ = sprintf(' class="%s"', item['_' + field + '_class']);
	            }
	            if (item['_' + field + '_rowspan']) {
	                rowspan_ = sprintf(' rowspan="%s"', item['_' + field + '_rowspan']);
	            }
	            if (item['_' + field + '_colspan']) {
	                colspan_ = sprintf(' colspan="%s"', item['_' + field + '_colspan']);
	            }
	            if (item['_' + field + '_title']) {
	                title_ = sprintf(' title="%s"', item['_' + field + '_title']);
	            }
	            cellStyle = calculateObjectValue(that.header,
	                that.header.cellStyles[j], [value, item, i], cellStyle);
	            if (cellStyle.classes) {
	                class_ = sprintf(' class="%s"', cellStyle.classes);
	            }
	            if (cellStyle.css) {
	                var csses_ = [];
	                for (var key in cellStyle.css) {
	                    csses_.push(key + ': ' + cellStyle.css[key]);
	                }
	                style = sprintf('style="%s"', csses_.concat(that.header.styles[j]).join('; '));
	            }
	
	            value = calculateObjectValue(column,
	                that.header.formatters[j], [value, item, i], value);
	
	            if (item['_' + field + '_data'] && !$.isEmptyObject(item['_' + field + '_data'])) {
	                $.each(item['_' + field + '_data'], function (k, v) {
	                    // ignore data-index
	                    if (k === 'index') {
	                        return;
	                    }
	                    data_ += sprintf(' data-%s="%s"', k, v);
	                });
	            }
	
	            if (column.checkbox || column.radio) {
	                type = column.checkbox ? 'checkbox' : type;
	                type = column.radio ? 'radio' : type;
	
	                text = [sprintf(that.options.cardView ?
	                    '<div class="card-view %s">' : '<td class="bs-checkbox %s">', column['class'] || ''),
	                    '<input' +
	                    sprintf(' data-index="%s"', i) +
	                    sprintf(' name="%s"', that.options.selectItemName) +
	                    sprintf(' type="%s"', type) +
	                    sprintf(' value="%s"', item[that.options.idField]) +
	                    sprintf(' checked="%s"', value === true ||
	                    (value && value.checked) ? 'checked' : undefined) +
	                    sprintf(' disabled="%s"', !column.checkboxEnabled ||
	                    (value && value.disabled) ? 'disabled' : undefined) +
	                    ' />',
	                    that.header.formatters[j] && typeof value === 'string' ? value : '',
	                    that.options.cardView ? '</div>' : '</td>'
	                ].join('');
	
	                item[that.header.stateField] = value === true || (value && value.checked);
	            } else {
	                value = typeof value === 'undefined' || value === null ?
	                    that.options.undefinedText : value;
	
	                text = that.options.cardView ? ['<div class="card-view">',
	                    that.options.showHeader ? sprintf('<span class="title" %s>%s</span>', style,
	                        getPropertyFromOther(that.columns, 'field', 'title', field)) : '',
	                    sprintf('<span class="value">%s</span>', value),
	                    '</div>'
	                ].join('') : [sprintf('<td%s %s %s %s %s %s %s>',
	                    id_, class_, style, data_, rowspan_, colspan_, title_),
	                    value,
	                    '</td>'
	                ].join('');
	
	                // Hide empty data on Card view when smartDisplay is set to true.
	                if (that.options.cardView && that.options.smartDisplay && value === '') {
	                    // Should set a placeholder for event binding correct fieldIndex
	                    text = '<div class="card-view"></div>';
	                }
	            }
	
	            html.push(text);
	        });
	
	        if (this.options.cardView) {
	            html.push('</td>');
	        }
	
	        html.push('</tr>');
	    }
	
	    // show no records
	    if (!html.length) {
	        html.push('<tr class="no-records-found">',
	            sprintf('<td colspan="%s">%s</td>',
	                this.$header.find('th').length, this.options.formatNoMatches()),
	            '</tr>');
	    }
	
	    this.$body.html(html.join(''));
	
	    if (!fixedScroll) {
	        this.scrollTo(0);
	    }
	
	    // click to select by column
	    this.$body.find('> tr[data-index] > td').off('click dblclick').on('click dblclick', function (e) {
	        var $td = $(this),
	            $tr = $td.parent(),
	            item = that.data[$tr.data('index')],
	            index = $td[0].cellIndex,
	            field = that.header.fields[that.options.detailView && !that.options.cardView ? index - 1 : index],
	            column = that.columns[getFieldIndex(that.columns, field)],
	            value = getItemField(item, field, that.options.escape);
	
	        if ($td.find('.detail-icon').length) {
	            return;
	        }
	
	        that.trigger(e.type === 'click' ? 'click-cell' : 'dbl-click-cell', field, value, item, $td);
	        that.trigger(e.type === 'click' ? 'click-row' : 'dbl-click-row', item, $tr);
	
	        // if click to select - then trigger the checkbox/radio click
	        if (e.type === 'click' && that.options.clickToSelect && column.clickToSelect) {
	            var $selectItem = $tr.find(sprintf('[name="%s"]', that.options.selectItemName));
	            if ($selectItem.length) {
	                $selectItem[0].click(); // #144: .trigger('click') bug
	            }
	        }
	    });
	
	    this.$body.find('> tr[data-index] > td > .detail-icon').off('click').on('click', function () {
	        var $this = $(this),
	            $tr = $this.parent().parent(),
	            index = $tr.data('index'),
	            row = data[index]; // Fix #980 Detail view, when searching, returns wrong row
	
	        // remove and update
	        if ($tr.next().is('tr.detail-view')) {
	            $this.find('i').attr('class', sprintf('%s %s', that.options.iconsPrefix, that.options.icons.detailOpen));
	            $tr.next().remove();
	            that.trigger('collapse-row', index, row);
	        } else {
	            $this.find('i').attr('class', sprintf('%s %s', that.options.iconsPrefix, that.options.icons.detailClose));
	            $tr.after(sprintf('<tr class="detail-view"><td colspan="%s"></td></tr>', $tr.find('td').length));
	            var $element = $tr.next().find('td');
	            var content = calculateObjectValue(that.options, that.options.detailFormatter, [index, row, $element], '');
	            if($element.length === 1) {
	                $element.append(content);
	            }
	            that.trigger('expand-row', index, row, $element);
	        }
	        that.resetView();
	    });
	
	    this.$selectItem = this.$body.find(sprintf('[name="%s"]', this.options.selectItemName));
	    this.$selectItem.off('click').on('click', function (event) {
	        event.stopImmediatePropagation();
	
	        var $this = $(this),
	            checked = $this.prop('checked'),
	            row = that.data[$this.data('index')];
	
	        if (that.options.maintainSelected && $(this).is(':radio')) {
	            $.each(that.options.data, function (i, row) {
	                row[that.header.stateField] = false;
	            });
	        }
	
	        row[that.header.stateField] = checked;
	
	        if (that.options.singleSelect) {
	            that.$selectItem.not(this).each(function () {
	                that.data[$(this).data('index')][that.header.stateField] = false;
	            });
	            that.$selectItem.filter(':checked').not(this).prop('checked', false);
	        }
	
	        that.updateSelected();
	        that.trigger(checked ? 'check' : 'uncheck', row, $this);
	    });
	
	    $.each(this.header.events, function (i, events) {
	        if (!events) {
	            return;
	        }
	        // fix bug, if events is defined with namespace
	        if (typeof events === 'string') {
	            events = calculateObjectValue(null, events);
	        }
	
	        var field = that.header.fields[i],
	            fieldIndex = getFieldIndexFromColumnIndex(that.columns, i);
	
	        if (that.options.detailView && !that.options.cardView) {
	            fieldIndex += 1;
	        }
	
	        for (var key in events) {
	            that.$body.find('>tr:not(.no-records-found)').each(function () {
	                var $tr = $(this),
	                    $td = $tr.find(that.options.cardView ? '.card-view' : 'td').eq(fieldIndex),
	                    index = key.indexOf(' '),
	                    name = key.substring(0, index),
	                    el = key.substring(index + 1),
	                    func = events[key];
	
	                $td.find(el).off(name).on(name, function (e) {
	                    var index = $tr.data('index'),
	                        row = that.data[index],
	                        value = row[field];
	
	                    func.apply(this, [e, value, row, index]);
	                });
	            });
	        }
	    });
	
	    this.updateSelected();
	    this.resetView();
	
	    this.trigger('post-body', data);
    };
}(jQuery);
