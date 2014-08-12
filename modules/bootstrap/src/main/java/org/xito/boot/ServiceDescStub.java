// Copyright 2007 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.boot;

import java.net.URL;

public class ServiceDescStub {
	
	private String name;
	private String href;
	private URL contextURL;
	
	public ServiceDescStub(String name, String href, URL contextURL) {
		
		this.name = name;
		this.href = (href == null || href.equals("")) ? null : href;
		this.contextURL = contextURL;
	}

	public URL getContextURL() {
		return contextURL;
	}

	public void setContextURL(URL contextURL) {
		this.contextURL = contextURL;
	}

	public String getHREF() {
		return href;
	}

	public void setHREF(String href) {
		this.href = href;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
