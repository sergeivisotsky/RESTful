/*
 * Copyright 2018-2019 the original author.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sergei.rest.service;

/**
 * Constants to be used in service layer
 *
 * @author Sergei Visotsky
 */
public final class Constants {

    /**
     * Hide from the public access
     */
    private Constants() {
    }

    public static final String CUSTOMER_NOT_FOUND = "Customer with this ID not found";
    public static final String ORDER_NOT_FOUND = "Order with this ID not found";
    public static final String PRODUCT_NOT_FOUND = "Product with this ID not found";
    public static final String PHOTO_NOT_FOUND = "Photo with this ID not found";
    public static final String FILE_NOT_FOUND = "File not found";

}
