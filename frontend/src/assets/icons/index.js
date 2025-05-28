// 导入所有SVG图标
const svgFiles = import.meta.glob('./*.svg', { eager: true })

// 注册所有图标
const icons = {}
for (const path in svgFiles) {
  const name = path.match(/([^/]+)\.svg$/)[1]
  icons[name] = svgFiles[path].default
}

export default icons 