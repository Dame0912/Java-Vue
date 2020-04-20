export default {

  transformTozTreeFormat(sNodes) {
    let i, l
    let r = []
    let tmpMap = {}
    for (i = 0, l = sNodes.length; i < l; i++) {
      tmpMap[sNodes[i].id] = sNodes[i]
    }
    for (i = 0, l = sNodes.length; i < l; i++) {
      let p = tmpMap[sNodes[i].pid]
      if (p && sNodes[i].id != sNodes[i].pid) {
        let children = this.nodeChildren(p)
        if (!children) {
          children = this.nodeChildren(p, [])
        }
        children.push(sNodes[i])
      } else {
        r.push(sNodes[i])
      }
    }
    return r
  },

  nodeChildren(node, newChildren) {
    if (typeof newChildren !== 'undefined') {
      node.children = newChildren
    }
    return node.children
  }
}
