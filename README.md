# JOGL Examples
# Since June 7, 2018, the repository will no longer be updated here. All updates and other projects will now be on the [GitLab](https://gitlab.com/congard/jogl-examples)
<br>JOGL (Java OpenGL) Examples

1. [Shaders with 2 matrices](https://github.com/congard/jogl-examples/tree/master/jogl-shaders-2matrices)
1. Shaders with 3 matrices
1. [Shaders with 3 matrices (Model-View-Projection) and simple lighting](https://github.com/congard/jogl-examples/tree/master/shaders-3matrices_mvp-simpe-lighting)
1. [Shaders with 3 matrices (Model-View-Projection) and simple lighting with several lamps](https://github.com/congard/jogl-examples/tree/master/shaders-3mat_mvp-simple-lighting_several-lamps)
1. [Shaders with 3 matrices (Model-View-Projection), simple lighting with several lamps and texture](https://github.com/congard/jogl-examples/tree/master/shaders-3mat_mvp-ssllighting-texture)
1. [Ambient+diffuse+specular colorful lighting with several lamps, animation and texture](https://github.com/congard/jogl-examples/tree/master/lighting_colorful_ambient_diffuse_specular-several_lamps-texture-animation)
1. [Ambient+diffuse+specular colorful lighting with several lamps, animation, texture and lamps prop](https://github.com/congard/jogl-examples/tree/master/lighting_colorful_ambient_diffuse_specular-several_lamps-texture-animation-lampsprop)
    <br><br>Small fix for lamps:
    <br>`// updating lamps eye space position`
		<br>`for (int i = 0; i<lamps.length; i++) lamps[i].calculateLampPosInEyeSpace(mViewMatrix);`
    <br>Add this to `display` method
    <br>This is necessary to add in the event that on your scene the lamps do not move, but the camera moves
    <br><br>
1. [Ambient+diffuse+specular colorful lighting with several lamps, animation, texture, lamps prop, attenuation and OBJ models loading](https://github.com/congard/jogl-examples/tree/master/lighting_colorful_ambient_diffuse_specular-several_lamps-texture-animation-lampsprop-attenuation-objmodels)
1. [Ambient+diffuse+specular colorful lighting with several lamps, animation, texture, lamps prop, attenuation and, models loading and lighting maps](https://github.com/congard/jogl-examples/tree/master/lighting_colorful_ambient_diffuse_specular-several_lamps-texture-animation-lampsprop-attenuation-objmodels-lightingmaps)
1. [+ Normal Mapping](https://github.com/congard/jogl-examples/tree/master/010-normalmapping)<br>[Video](https://www.youtube.com/watch?v=l_JgyE9BKo0)
1. [+ Shadow Mapping with soft shadows](https://github.com/congard/jogl-examples/tree/master/011-shadowmapping)
