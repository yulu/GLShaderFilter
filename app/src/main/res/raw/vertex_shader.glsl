/*
    Copyright 2014 YU LU

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

uniform mat4 uTransformM;
uniform mat4 uOrientationM;
uniform vec2 uRatio;
uniform vec2 uRatioPreview;
attribute vec2 aPosition;

varying vec2 vTextureCoord;

void main(){
	gl_Position = vec4(aPosition, 0.0, 1.0);
	vTextureCoord = (uTransformM * ((uOrientationM * gl_Position + 1.0)*0.5)).xy;
	gl_Position.xy *= uRatio / uRatioPreview;
}