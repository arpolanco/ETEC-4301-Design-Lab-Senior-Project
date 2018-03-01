from vector import *
from matrix import *
import math
import pygame

class Face(object):
    def __init__(self, L, mat, parent):
        self.vList = L
        self.mat = mat
        self.parent = parent
        self.transform = identity(4)
        total = VectorN(0,0,0,0)
        for v in L:
            total += v
        self.center = total / len(L)
        a = L[0] - L[1]
        b = L[2] - L[1]
        normal = b.truncate(3).cross(a.truncate(3))
        self.normal = VectorN(normal[0], normal[1], normal[2], 0).normalized()
        self.modified = VectorN(0,0,0,0)
    def __lt__(self, other):
        return self.modified[2] > other.modified[2]
    def render(self, surf, mat, cam = None, lights = None, parentTrans = identity(4), fill = False, renderNormals = False):
        norm = (self.normal * mat).normalized()
        if norm.dot(cam.zAxis) <= 0:
            if renderNormals:
                p = self.center * mat
                p2 = (self.center + self.normal * 0.5) * mat
                pygame.draw.circle(surf, (255,255,255), p.i[:2], 2)
                pygame.draw.line(surf, (255,255,255), p.i[:2], p2.i[:2], 2)
            L = []
            for v in self.vList:
                L.append((v * mat).truncate(2))
            if not fill or cam == None or lights == None:
                pygame.draw.polygon(surf, (self.parent.matDict[self.mat]["diff"] * 255).i, L,1)
            else:
                color = self.parent.matDict[self.mat]["amb"].pairwiseMult(Vector3(0.1,0.1,0.1))
                for l in lights:
                    lDir = (l.pos - self.center).normalized()
                    dStr = lDir.dot(norm)
                    if dStr > 0:
                        color += dStr * self.parent.matDict[self.mat]["diff"].pairwiseMult(l.diff)
                    R = (2 * (lDir.dot(norm) * norm)) - lDir
                    V = (cam.pos - self.center).normalized()
                    sStr = V.dot(R)
                    if sStr > 0:
                        color += (sStr ** self.parent.matDict[self.mat]["hard"]) * self.parent.matDict[self.mat]["spec"].pairwiseMult(l.spec)
                color = color.clamp()
                pygame.draw.polygon(surf, (color * 255).i, L,0)
class RasterizerCamera(object):
    def __init__(self, pos, coi):
        self.pos = pos
        self.coi = coi
        self.zAxis = (coi - pos).normalized()
class RasterizerLight(object):
    def __init__(self, pos, diff, spec):
        self.pos = pos
        self.diff = diff
        self.spec = spec
class RasterizerObject(object):
    def __init__(self, filename = None, mtlFilename = None):
        self.vList = []
        self.fList = []
        self.matDict = {}
        self.children = []
        self.parent = None
        self.transform = identity(4)
        self.renderNormals = False
        curMat = ""
        if filename != None and mtlFilename != None:
            with open(filename) as fp:
                for line in fp:
                    line = line.strip()
                    L = line.split(" ")
                    if L[0] == "v":
                        self.vList.append(VectorN(L[1], L[2], L[3], 1))
                    elif L[0] == "f":
                        v = []
                        for i in range(1,len(L)):
                            v.append(int(L[i].split("/")[0]))
                        p = []
                        for i in range(len(v)):
                            p.append(self.vList[v[i] - 1])
                        self.fList.append(Face(p, curMat, self))
                    elif L[0] == "usemtl":
                        curMat = L[1]
            with open(mtlFilename) as fp:
                curHardness = 0
                curAmb = Vector3(0,0,0)
                curDiff = Vector3(0,0,0)
                for line in fp:
                    line = line.strip()
                    L = line.split(" ")
                    if L[0] == "newmtl":
                        curMat = L[1]
                        print(curMat)
                        self.matDict[curMat] = {}
                    elif L[0] == "Ns":
                        self.matDict[curMat]["hard"] = float(L[1])
                    elif L[0] == "Ka": 
                        self.matDict[curMat]["amb"] = VectorN(L[1], L[2], L[3])
                    elif L[0] == "Kd":
                        self.matDict[curMat]["diff"] = VectorN(L[1], L[2], L[3])
                    elif L[0] == "Ks":
                        self.matDict[curMat]["spec"] = VectorN(L[1], L[2], L[3])

        
    def addChild(self, child):
        self.children.append(child)
        child.parent = self
    def getFullTransform(self):
        if self.parent != None:
            return self.transform * self.parent.getFullTransform()
        return self.transform
    def getFaces(self):
        L = []
        L.append([self.fList, self])
        for child in self.children:
            L += child.getFaces()
        return L
    def render(self, surf, cam = None, lights = None, parentTrans = identity(4), fill = False):
        mat = self.transform * parentTrans
        for child in self.children:
            child.render(surf, cam, lights, mat, fill)
        v = VectorN(0,0,-1,0)
        self.fList.sort()
        for f in self.fList:
            norm = (f.normal * mat).normalized()
            if norm.dot(cam.zAxis) <= 0:
                if self.renderNormals:
                    p = f.center * mat
                    p2 = (f.center + f.normal * 0.5) * mat
                    pygame.draw.circle(surf, (255,255,255), p.i[:2], 2)
                    pygame.draw.line(surf, (255,255,255), p.i[:2], p2.i[:2], 2)
                L = []
                for v in f.vList:
                    L.append((v * mat).truncate(2))
                if not fill or cam == None or lights == None:
                    pygame.draw.polygon(surf, (self.matDict[f.mat]["diff"] * 255).i, L,1)
                else:
                    color = self.matDict[f.mat]["amb"].pairwiseMult(Vector3(0.1,0.1,0.1))
                    for l in lights:
                        lDir = (l.pos - f.center).normalized()
                        dStr = lDir.dot(norm)
                        if dStr > 0:
                            color += dStr * self.matDict[f.mat]["diff"].pairwiseMult(l.diff)
                        R = (2 * (lDir.dot(norm) * norm)) - lDir
                        V = (cam.pos - f.center).normalized()
                        sStr = V.dot(R)
                        if sStr > 0:
                            color += (sStr ** self.matDict[f.mat]["hard"]) * self.matDict[f.mat]["spec"].pairwiseMult(l.spec)
                    color = color.clamp()
                    pygame.draw.polygon(surf, (color * 255).i, L,0)
class Rasterizer(object):
    def __init__(self, cam = None, lights = None):
        self.objList = []
        self.cam = cam
        self.lights = lights
    def add_obj(self, obj):
        self.objList.append(obj)
    def render(self, surf, fill = False):
        fList = []
        matList = {}
        for obj in self.objList:
            for lst in obj.getFaces():
                L, o = lst
                fList += L
                mat = o.getFullTransform()
                matList[o] = mat
                for f in L:
                    f.transform = mat
                    f.modified = f.center * mat
        if fill:
            fList.sort()
        for f in fList:
            f.render(surf, matList[f.parent], self.cam, self.lights, fill = fill)
