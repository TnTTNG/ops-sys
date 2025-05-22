<template>
  <!-- 设置整个div的高度为视口高度100%，并使其内容垂直和水平居中
  style="height: 100vh;"：将div的高度设置为视口（viewport）的高度，即屏幕的高度。
  display: flex;：使用Flexbox布局来排列子元素。
  align-items: center;：使子元素在其交叉轴上（这里是垂直方向）居中对齐。
  justify-content: center;：使子元素在其主轴上（这里是水平方向）居中对齐。
  src="@/assets/img/404 Error with a cute animal-pana.jpg"：指定图片的路径。这里的@是Vue CLI项目中的别名，指向项目的src目录。
  alt="Cute Animal 404 Error Image"：为图片提供替代文本，当图片无法加载时会显示这段文字。-->
  <div style="height: 100vh; display: flex; align-items: center; justify-content: center">
    <!-- 显示一张图片，宽度设置为其容器宽度的50% -->
    <div style="width: 40%">
      <img style="width: 100%" src="@/assets/img/404%20Error%20with%20a%20cute%20animal-pana.jpg" alt="">
      <div style="text-align: center; padding: 20px 0; font-size: 20px;"><a style="color: #181717" href="/">返回主页</a></div>
    </div>
  </div>
</template>
<script setup>
</script>