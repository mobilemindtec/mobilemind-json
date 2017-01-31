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


import br.com.mobilemind.api.utils.DateUtil;
import br.com.mobilemind.api.json.annotations.JsonColumn;
import br.com.mobilemind.api.json.annotations.JsonEntity;
import java.util.Date;

/**
 *
 * @author Ricardo Bocchi
 */
@JsonEntity
public class Person {

    @JsonColumn
    private int id = 5;
    @JsonColumn
    private String name = "JSON Boy";
    @JsonColumn
    private PersonType type = PersonType.F;
    @JsonColumn
    private Long age = 50L;
    @JsonColumn
    private double year = 1.2;
    @JsonColumn
    private boolean status = true;
    @JsonColumn(patternToDateConverter=DateUtil.PATTER_DATE_FORMAT)
    private Date date = new Date();
    @JsonColumn(isComplexType = true)
    private ComplexObject complexObject = new ComplexObject();

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public PersonType getType() {
        return type;
    }

    public void setType(PersonType type) {
        this.type = type;
    }

    public double getYear() {
        return year;
    }

    public void setYear(double year) {
        this.year = year;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ComplexObject getComplexObject() {
        return complexObject;
    }

    public void setComplexObject(ComplexObject complexObject) {
        this.complexObject = complexObject;
    }
}
