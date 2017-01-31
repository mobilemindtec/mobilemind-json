package br.com.mobilemind.api.json.test.tools;

/*
 * #%L
 * Mobile Mind - JSON
 * %%
 * Copyright (C) 2012 Mobile Mind Empresa de Tecnologia
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import br.com.mobilemind.api.json.annotations.JsonEntity;
import br.com.mobilemind.api.json.annotations.JsonColumn;

/**
 *
 * @author Ricardo Bocchi
 */
@JsonEntity
public class MethodInject {

    @JsonColumn(useJavaBean = true)
    private String value = "sabão";
    public boolean injectedSet;
    public boolean injectedGet;

    public String getValue() {
        injectedGet = true;
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        injectedSet = true;
    }
}
