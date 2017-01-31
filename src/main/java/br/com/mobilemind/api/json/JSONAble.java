package br.com.mobilemind.api.json;

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


import java.util.Collection;
import java.util.List;

/**
 *
 * @author Ricardo Bocchi
 */
public interface JSONAble<T> {

    JSONObject toJSON() throws JSONException;

    JSONArray toJSONArray(Collection<T> list) throws JSONException;

    T fromJSON(JSONObject json) throws JSONException;

    List<T> fromJSONArray(JSONArray array) throws JSONException;

    T fromJSON(String json) throws JSONException;

    List<T> fromJSONArray(String array) throws JSONException;
}
