/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zuowei.config;
import com.zuowei.func.Property;

public class UserDao {
	public static final String TABLE_NAME = "user";
	@Property(primaryKey = true,type = "text")
	public static final String COLUMN_NAME_ID = "userName";
	@Property(type = "text")
	public static final String COLUMN_NAME_NICK = "nick";
	@Property(type = "text")
	public static final String COLUMN_NAME_AVATAR = "avatar";
	@Property(type = "text")
	public static final String COLUMN_NAME_OBJECTID = "objectId";
	@Property(type = "text")
	public static final String COLUMN_NAME_COLLEGE = "college";
	@Property(type = "integer")
	public static final String COLUMN_NAME_SEX = "sex";
	@Property(type = "text")
	public static final String COLUMN_NAME_INTEREST = "interest";
}
